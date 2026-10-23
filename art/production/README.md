# WO #002 production asset record

The approved replacement supplied as `2515.png` is byte-identical to the transparent sheet recovered from the interrupted session.

- Source: 1536×1024 RGBA; alpha 0–254; 780,245 fully transparent pixels.
- SHA-256: `d9bf6e66242273b014ced41e12b744caed66f7817d2382ec567a56455c51feb6`.
- Six 512×512 portraits and one 512×1024 full-body cutout were recovered locally.
- All seven have fully transparent borders. Six-portrait contact sheets were inspected over white and dark backgrounds.
- Extraction uses the seven separate original drawings, preserves source alpha, and removes only stray alpha <=4/255 pixels. All portraits share a uniform scale, neck anchor, and shoulder baseline; natural source head tilts remain.
- No generation, reconstruction, recoloring, or ear deformation was used.

`extraction.json` records source components, crop bounds, runtime bounds, and alignment anchors. `scripts/extract_character_assets.py` reproduces the extraction using Pillow, NumPy, and SciPy (developer tooling only). The original source is preserved unmodified separately from runtime sprites.

All seven PNGs are published under `app/src/main/res/drawable-nodpi/`. The six portraits are used by the widget; the full-body illustration (including satchel, hands, feet, and hair) is retained as a reference resource. The untouched source is archived at `art/source/grump-approved-transparent-sheet.png`.

All portrait artwork lies at least 38 pixels inside the horizontal canvas edges and 45 pixels inside the bottom edge. Source head tilts and eye positions vary intentionally; scale and neck/shoulder anchors align rather than deforming the original faces. Source drawings are approximately 325–380 pixels wide, so 512-pixel exports use modest uniform scaling, not invented detail.

The source itself has near-opaque character interiors (alpha up to 254 rather than 255) and antialiased ink edges; these are preserved. There are no baked background rectangles, checkerboards, ground shadows, labels, or neighboring figures in the runtime sprites. A tiny source opacity difference is intentional, not forcibly repainted.

Runtime portraits were inspected on white and dark backgrounds and at widget scale. The original 64 dp image region, bubble XML, and padding remain unchanged. Original hand-drawn head tilts remain visible on switching; no facial feature normalization/redraw was performed. Pixel visual acceptance is still required. PR #2 remains unmerged pending user approval.
