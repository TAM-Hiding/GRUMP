# WO #002 — production artwork request

**Status: resolved by the approved transparent replacement `2515.png`. This document preserves the earlier request and requirements for future replacements. See [the production record](../art/production/README.md).** The approved design remains authoritative. This request concerns asset preparation, not character redesign.

## Source assessment

The supplied `2507.png` is a 1536 × 1024 concept sheet. Inspection confirms an RGBA file with alpha values ranging from 1 to 253 across the sheet; the alpha channel does not cleanly separate the paper from the drawings. Paper samples retain substantial opacity (for example, pixel 600,300 is RGBA 239,236,226,195). Merely cropping the portraits would retain visible cream rectangles.

The six expression illustrations are approximately 190–210 pixels wide each. Their fine ink hair, pale edge washes, and fabric edges merge with the textured paper. Enlarging them to a 512-pixel runtime canvas would not restore missing edge detail. Background removal cannot be accepted confidently without eroding those details or leaving paper halos on the home screen. No production cutouts have been accepted, and no generated reconstruction has been used.

| Asset | Blocking preparation issue |
| --- | --- |
| `grump_idle.png` | Paper remains behind the fine hair, full ear outlines, and soft shoulder edges. |
| `grump_annoyed.png` | Same paper/edge separation issue; a consistent anchor must preserve the tilted pose. |
| `grump_exasperated.png` | Same paper/edge issue; the separate decorative breath symbol must not contaminate the character crop. |
| `grump_suspicious.png` | Fine flyaway hair, both ear silhouettes, and shoulder wash need a clean alpha matte. |
| `grump_impressed.png` | Sparse hair, pale facial/ear edges, and shoulder wash need a clean alpha matte. |
| `grump_unimpressed.png` | Wide ear silhouette, fine hair, and clothing wash need a clean alpha matte. |
| `grump_fullbody.png` | Paper and ground shadow remain around ragged fabric, fingers, feet, and the gaps between limbs. |

The illustrations are mostly spatially separate; neighboring labels can be excluded with cropping. That alone does not solve the transparency and edge-fidelity problem. The stop is a fidelity decision, not a claim that the sheet has no useful reference value.

The original user attachment remains unchanged, separate from runtime assets. Its identity and checksum are recorded in [CHARACTER.md](../CHARACTER.md).

## Replacement pack specifications

Please provide **six separate transparent head-and-shoulders PNG files**, using the exact approved character:

1. `grump_idle.png`: default exhausted expression.
2. `grump_annoyed.png`: mild irritation, still exhausted.
3. `grump_exasperated.png`: eyes closed, sighing; no detached breath cloud needed.
4. `grump_suspicious.png`: skeptical sideways glance.
5. `grump_impressed.png`: reluctant admiration.
6. `grump_unimpressed.png`: intense disappointment, without energetic anger.

For each expression:

- Prefer **1024 × 1024 source PNGs**; final runtime versions will be **512 × 512**, all the same size.
- Use genuine PNG alpha: fully transparent empty pixels, opaque character interiors, and soft alpha only where needed for natural ink/watercolor edges. No cream paper, baked checkerboard, scenery, ground shadow, text, labels, borders, mockups, or neighboring art.
- Use the same head-and-shoulders framing, character scale, eye-line anchor, and neck/shoulder baseline. Expression and natural head tilt may vary; avoid whole-character size/position jumps.
- Keep both complete ear silhouettes and every hair tip inside the canvas. At 1024 × 1024, leave at least **48 pixels of transparent clearance** outside the outermost ear/hair/shoulder point on each side; no paint should touch a canvas edge. Enlarge the canvas rather than shortening or deforming ears.
- Preserve the elderly face, eyelids, thin neck, sparse hair, drooping ears, pinkish-brown inner ears, sage/olive skin, scarf, ragged clothing, earthy colors, and original ink-and-watercolor texture.
- Preserve all canonical design details. Do not make him cuter, angrier, cleaner, younger, more optimistic, or more cartoonish.

Also provide `grump_fullbody.png` on a **1024 × 1536 transparent canvas** (or larger at the same aspect ratio), with at least 48 pixels of clearance around the entire figure. Include both ears, hair, hands, feet, clothing, and satchel. Exclude paper, annotations, floor line, and cast ground shadow. This asset is reference/future-screen material, not the widget portrait.

If separate files cannot be supplied, provide one high-resolution sheet with six equal, non-overlapping square expression cells, no text or decoration, genuine transparency, and the same clear margins inside each cell. Separate files are preferred.

## Gate before integration resumes

1. Check the alpha channel, and inspect edges over white, dark, and checkerboard backgrounds.
2. Verify all six expressions and the full-body reference against the approved sheet. No invented details or clipped ears.
3. Align and downscale without stretching; compare portraits at their actual widget display size.
4. Only then integrate the complete expression set under `res/drawable-nodpi/`.
5. Preserve the current rounded bubble, colors, centered readable text, and tap interactions. Stay within launcher widget boundaries.
6. Run build/resource checks and interaction/layout validation. Open a reviewable PR and obtain approval before merging.

The replacement artwork has now passed alpha and visual inspection. v0.2.0 integrates all six portraits and preserves the full-body asset. Keep these gates for future artwork changes.
