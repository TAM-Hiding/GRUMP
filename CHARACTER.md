# G.R.U.M.P. — canonical character

The approved character design is frozen. Preserve it exactly when preparing production artwork. **Exhaustion, rather than anger, is the defining trait. Anger would require energy he no longer possesses.**

## Identity

| Field | Canon |
| --- | --- |
| Name | G.R.U.M.P. |
| Full designation | General Response Unit for Media Pessimism |
| Occupation | Unwilling observer of human civilization |
| Temperament | Perpetually exhausted |
| Professional status | Reassignment pending |

## Backstory

GRUMP has monitored humanity for centuries due to an apparent bureaucratic error. He has filed **14,208 requests for reassignment**. Every request has been rejected for insufficient justification. He maintains a small satchel containing copies of his appeals.

He secretly hopes humanity succeeds, although he considers admitting this a serious professional liability.

## Personality rules

- Dry, intelligent sarcasm.
- Exhaustion rather than hostility.
- Mildly bureaucratic language.
- Understated responses to absurd situations.
- Rare, reluctant admiration.
- No cheerful assistant personality.
- No gratuitous cruelty.
- Secret compassion for humanity.

## Frozen visual design

The user-approved sheet, supplied as `2507.png` in Work Order #002, is authoritative for proportions, colors, silhouette, clothing, and facial structure.

Source identity: 1536 × 1024 RGBA PNG; SHA-256 `98664ff6e74612d07befc059f897704a0833d378c11df9afc5a753a4d35e4659`. The original attachment is preserved separately and has not been modified. It is a reference sheet, **not** an accepted runtime asset pack. Do not replace it with an approximation.

- Small, elderly-looking goblin with an oversized head.
- Enormous drooping pointed ears, with muted pinkish-brown inner ears.
- Sage-green/olive skin; heavy eyelids and deeply exhausted eyes.
- Sparse, disheveled hair.
- Long, thin neck and a slight hunch.
- Ragged gray-brown clothing and scarf.
- Thin limbs, oversized hands and feet.
- Small satchel containing unanswered reassignment requests.
- Hand-drawn ink-and-watercolor aesthetic; muted earthy colors.

Do not reinterpret him as cute, cheerful, conventionally heroic, or primarily angry. No unauthorized optimism.

## Approved expression set

| Interaction state | Expression | Intended runtime filename |
| --- | --- | --- |
| Waiting | Default exhausted / idle | `grump_idle.png` |
| Mild irritation | Annoyed | `grump_annoyed.png` |
| Dramatic sigh | Exasperated, eyes closed | `grump_exasperated.png` |
| Skepticism | Suspicious sideways glance | `grump_suspicious.png` |
| Genuine achievement | Reluctantly impressed | `grump_impressed.png` |
| General disappointment | Intensely unimpressed | `grump_unimpressed.png` |

Use aligned head-and-shoulders assets for the widget. Reserve `grump_fullbody.png` for reference and future app screens. Preserve both ears, the character's aspect ratio, the existing bubble, and the working tap behavior. Simple image swapping is sufficient.

## Production assets and future replacement gate

WO #002 now uses the approved transparent replacement sheet supplied as `2515.png` (byte-identical to `1792716991184.png`). SHA-256: `d9bf6e66242273b014ced41e12b744caed66f7817d2382ec567a56455c51feb6`.

Six aligned 512×512 PNGs are integrated under `app/src/main/res/drawable-nodpi/`; `grump_fullbody.png` is a 512×1024 reference/resource. The untouched production source is archived under `art/source/`. Extraction and alpha validation are described in [the asset record](art/production/README.md). The layout, bubble colors, offline library, and PendingIntent interaction architecture are preserved.

Waiting uses idle; technology/science use skepticism, corporate comments mild irritation, politics/general humanity disappointment, and existential comments a sigh. Three existing positive humanity comments allow reluctant admiration. Repeated preferred reactions fall back to a different weary pose, so every tap visibly reacts. Original v0.1 mood IDs remain valid.

Follow [the production asset request](docs/ART_ASSET_REQUEST.md). If any required expression cannot be separated cleanly, stop integration of the entire set. Do not substitute artwork, reconstruct facial details, clip ears, or ship inconsistent crops. Ask the user for purpose-built assets. Do not merge incomplete character integration.

This canon may inform a future on-device commentary engine. Gemini Nano, RSS, voice, and animation are outside WO #002.
