# WO #002 recovery checkpoint

The approved replacement supplied as `2515.png` is byte-identical to the transparent sheet recovered from the interrupted session.

- Source: 1536×1024 RGBA; alpha 0–254; 780,245 fully transparent pixels.
- SHA-256: `d9bf6e66242273b014ced41e12b744caed66f7817d2382ec567a56455c51feb6`.
- Six 512×512 portraits and one 512×1024 full-body cutout were recovered locally.
- All seven have fully transparent borders. Six-portrait contact sheets were inspected over white and dark backgrounds.
- Extraction uses the seven separate original drawings, preserves source alpha, and removes only stray alpha <=4/255 pixels. All portraits share a uniform scale, neck anchor, and shoulder baseline; natural source head tilts remain.
- No generation, reconstruction, recoloring, or ear deformation was used.

`extraction.json` records source components, crop bounds, runtime bounds, and alignment anchors. `scripts/extract_character_assets.py` reproduces the extraction using Pillow, NumPy, and SciPy (developer tooling only). The original source is preserved unmodified separately from runtime sprites.

This checkpoint records recovered progress; PNG publication and Android integration follow in subsequent commits. PR #2 remains unmerged.
