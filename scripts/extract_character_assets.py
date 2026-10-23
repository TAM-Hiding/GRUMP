# Deterministic extraction of the approved transparent sheet.
# Developer-only prerequisites: Pillow, numpy, scipy. No app/build dependency.
# No generation, recoloring, or reconstruction. Alpha <=4/255 removes faint stray background noise.
from PIL import Image
import numpy as np
from scipy.ndimage import label, distance_transform_edt
from pathlib import Path
import json
repo=Path(__file__).resolve().parents[1]
src=Image.open(repo/'art/source/grump-approved-transparent-sheet.png').convert('RGBA')
a=np.array(src)
labels,n=label(a[:,:,3]>32)
# The seven large disconnected components are the seven original drawings.
components=[]
for ident in range(1,n+1):
 ys,xs=np.where(labels==ident)
 if len(xs)>10000: components.append((ident,int(xs.min()),int(ys.min()),int(xs.max()),int(ys.max())))
assert len(components)==7
# Assign faint antialias/hair pixels to the nearest original drawing, preserving alpha.
seeds=np.zeros(labels.shape,dtype=np.int16)
for ident,*_ in components: seeds[labels==ident]=ident
_,nearest=distance_transform_edt(seeds==0,return_indices=True)
owners=seeds[tuple(nearest)]
regions={
 'idle':(210,541,27,182), 'annoyed':(583,541,415,195),
 'exasperated':(938,532,743,193), 'suspicious':(214,908,24,558),
 'impressed':(587,908,407,557), 'unimpressed':(926,908,737,555),
 'fullbody':(0,0,1091,76)
}
manifest={}
for name,(anchor,baseline,x,y) in regions.items():
 ident=next(c[0] for c in components if c[1]==x and c[2]==y)
 pixels=a.copy();pixels[owners!=ident,3]=0
 pixels[pixels[:,:,3]<=4,3]=0
 # Discard RGB only where alpha is already exactly zero (prevents hidden brown RGB bleed).
 pixels[pixels[:,:,3]==0,:3]=0
 isolated=Image.fromarray(pixels)
 bbox=isolated.getbbox()
 isolated=isolated.crop(bbox)
 if name=='fullbody':
  canvas=Image.new('RGBA',(512,1024))
  isolated.thumbnail((464,976),Image.Resampling.LANCZOS)
  canvas.alpha_composite(isolated,((512-isolated.width)//2,(1024-isolated.height)//2))
 else:
  scale=1.10
  isolated=isolated.resize((round(isolated.width*scale),round(isolated.height*scale)),Image.Resampling.LANCZOS)
  canvas=Image.new('RGBA',(512,512))
  left=round(256+(bbox[0]-anchor)*scale)
  top=round(464+(bbox[1]-baseline)*scale)
  assert left>=0 and top>=0 and left+isolated.width<=512 and top+isolated.height<=512,(name,bbox,left,top,isolated.size)
  canvas.alpha_composite(isolated,(left,top))
 output=repo/'app/src/main/res/drawable-nodpi'/f'grump_{name}.png'
 canvas.save(output,optimize=True)
 alpha=np.array(canvas.getchannel('A'))
 assert alpha[0].max()==0 and alpha[-1].max()==0 and alpha[:,0].max()==0 and alpha[:,-1].max()==0
 manifest[name]={'canvas':canvas.size,'source_component':ident,'source_bbox':bbox,'runtime_bbox':canvas.getbbox(),'source_neck_anchor':anchor,'source_shoulder_baseline':baseline}
 print(name,canvas.size,canvas.getbbox(),output.stat().st_size)
(repo/'art/production/extraction.json').write_text(json.dumps(manifest,indent=2)+'\n')
