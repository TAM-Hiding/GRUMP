# WO #003 full-body asset record

Five approved uploads contain six figures. Source PNGs are archived byte-for-byte
in `art/source/full-body/`; checksums and source alpha counts are in `extraction.json`.

| Expression | Original | Region |
| --- | --- | --- |
| Idle / Waiting Room | 2527.png | Whole image; ticket 14,208 |
| Annoyed / Lean | 2526.png | Left figure |
| Exasperated / Existential Backbend | 2526.png | Right figure |
| Suspicious / Double Take | 2528.png | Whole image |
| Impressed / Exception | 2525.png | Whole image |
| Unimpressed / Verdict | 2529.png | Whole image |

All five inputs are RGBA with genuine alpha (0–254, except suspicious 0–255).
No baked checkerboard, rectangular background, or ground shadow was observed.
Suspicious has a few hair/toe antialias pixels on the original top/bottom edge;
we preserve everything present, but cannot reconstruct anything outside the source.
No additional alternate was supplied. All previous portraits/reference remain intact.

`python scripts/prepare_full_body_assets.py` reproduces 512×768 RGBA runtime PNGs,
with uniform scaling per figure, 12px canvas clearance and feet at y=756.
Only alpha <=4 noise is removed; no regeneration, recoloring, or stretching.
The double sheet is split at x=810 in its transparent gutter. No figure pixels
cross the split. Every resource border is transparent.

`WidgetArtwork` contains explicit transparent-gutter crops and approximate head
anchors per pose. Cropping removes empty padding only, keeps a 12px guard, and
lets the narrower upright figures appear larger. ImageView `fitEnd` preserves
aspect ratio and puts the feet at a shared lower edge. The forward lean is
naturally lower/wider. The bubble stays in one right-hand region while its tail
moves vertically toward the head; the regions never overlap. One unioned Canvas
path supplies the fill and outline, avoiding a doubled tail border. The small
bitmap background is safe in RemoteViews; text remains a native accessible
TextView with uniform auto-sizing. Drawing is capped at 512px per axis.

Preferred size is 2×3 launcher cells; minimum 160×220dp. Smaller old placements
should be resized. The narrow two-column format inevitably makes full-body faces
small and long comments use smaller text. Enlarge horizontally for readability.
Actual Pixel launcher placement, wallpaper contrast, and font preference need
physical acceptance testing. Android instrumentation exports light/dark previews
of every pose at 160×220, 180×280, and 300×220dp in its CI artifact.
