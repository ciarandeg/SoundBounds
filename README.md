# SoundBounds

SoundBounds is a region-based soundtracking mod for Minecraft. It blocks
all vanilla in-game music, and allows the user to define complex and
nested regions, each with their own customizable playlist.

## Features

- Smooth, two-second fade when exiting a region
- Compound region support (regions with multiple bounding boxes)
- Dimension-specific regions
- Volume control using vanilla options screen
- Swappable resource packs to supply assets
- Command for checking current-playing song (`/sb now-playing`)
- Fully-featured command-line interface allows users to manipulate and
    query regions, playlists and songs
- Built within Minecraft's own OpenAL context to provide maximum
    possible compatibility
- Playlists can be shuffled or sequential
- Songs can be non-looping, 1-part loops, or loops with a non-looping
    intro

## Setup

In order to use SoundBounds, you (and everyone on your server) must have
a SoundBounds-compatible resource pack enabled. An admin must run `/sb
sync-meta` in-game to make their client-side metadata the server's
"official" copy. This is to prevent synchronization issues; players will
be warned on server-join if their metadata doesn't match the server's.

## Resource pack format

SoundBounds does not rely on vanilla Minecraft's `sounds.json` file.
Instead, create an `assets/soundbounds` folder containing an `sb.json`
file (where you'll keep all your metadata) and a `music` folder (where
you'll keep all the assets referenced in your metadata).

### Metadata format

I will put up an example pack sometime soon. For the meantime, check out
the metadata spec in `JsonMeta.kt`.

## A note on Y coordinates

The player's Y position in-game refers to the position of their feet,
not their head. When assigning regions, keep in mind that in order for
the sound not to fade out when the player jumps, the bounding box must
be a minimum of three blocks tall Additionally, you can omit the y
coordinate entirely from a region's coordinate arrays in order to have
that axis be unbounded (as in the third example above).

## Todos/planned features

- Search for songs based on artist and title
- Check for regions with mismatched data on server startup
- Streaming from disk
- More complex/adaptive soundtracking

### Bugfixes

- Don't spend time fading out from an idling playlist

