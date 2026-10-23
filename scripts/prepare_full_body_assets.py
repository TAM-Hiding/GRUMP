"""WO003: lossless source archive -> aligned runtime canvases; no redraw.
Run from the repo root with Pillow. Only alpha <=4 speckle is removed.
"""
from pathlib import Path
from PIL import Image
import hashlib, json
SOURCE = Path('art/source/full-body')
OUT = Path('app/src/main/res/drawable-nodpi')
# Split in the completely transparent gutter, never through either silhouette.
POSES = {
    'idle': ('03-2527.png', None),
    'annoyed': ('02-2526.png', (0, 0, 810, 1024)),
    'exasperated': ('02-2526.png', (810, 0, 1536, 1024)),
    'suspicious': ('04-2528.png', None),
    'impressed': ('01-2525.png', None),
    'unimpressed': ('05-2529.png', None),
}
report = {'canvas': [512, 768], 'feet_baseline': 756, 'poses': {}}
for name, (filename, region) in POSES.items():
    original = Image.open(SOURCE / filename)
    assert original.mode == 'RGBA'
    alpha = original.getchannel('A')
    assert alpha.getextrema()[0] == 0
    im = original.crop(region) if region else original.copy()
    a = im.getchannel('A').point(lambda v: 0 if v <= 4 else v)
    im.putalpha(a)
    bounds = a.getbbox()
    im = im.crop(bounds)
    scale = min(488 / im.width, 744 / im.height)
    size = (round(im.width * scale), round(im.height * scale))
    im = im.resize(size, Image.Resampling.LANCZOS)
    canvas = Image.new('RGBA', (512, 768))
    offset = ((512 - im.width) // 2, 756 - im.height)
    canvas.paste(im, offset)
    canvas.save(OUT / f'grump_full_{name}.png', optimize=True)
    report['poses'][name] = dict(source=filename, sha256=hashlib.sha256((SOURCE / filename).read_bytes()).hexdigest(),
        source_size=original.size, source_alpha=alpha.getextrema(), transparent_pixels=alpha.histogram()[0],
        source_region=region, crop=bounds, scale=scale, offset=offset, runtime_bounds=canvas.getchannel('A').getbbox(),
        # Approximate head center; renderer points the tail here without moving the bubble.
        head_y=round(offset[1] + size[1] * .17))
Path('art/production/full-body/extraction.json').write_text(json.dumps(report, indent=2) + '\n')
print(json.dumps(report, indent=2))
