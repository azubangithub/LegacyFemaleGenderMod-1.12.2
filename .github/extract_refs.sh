#!/usr/bin/env bash

# Extract the branch name from $GITHUB_REF, replacing any slash characters with dashes
ref="${GITHUB_REF#refs/heads/}" && echo "branch=${ref////-}" >> $GITHUB_OUTPUT

# Extract versions from gradle.properties
mc_version=$(grep '^mc_version' gradle.properties | cut -d'=' -f2 | tr -d ' ')
mod_version=$(grep '^mod_version' gradle.properties | cut -d'=' -f2 | tr -d ' ')
forge_version=$(grep '^forge_version' gradle.properties | cut -d'=' -f2 | tr -d ' ')

echo "mc_version=${mc_version}" >> $GITHUB_OUTPUT
echo "mod_version=${mod_version}" >> $GITHUB_OUTPUT

### Build version summary
echo "## Version summary" >> $GITHUB_STEP_SUMMARY
echo "" >> $GITHUB_STEP_SUMMARY
echo "- Mod: LFGM v${mod_version}" >> $GITHUB_STEP_SUMMARY
echo "- Minecraft: ${mc_version}" >> $GITHUB_STEP_SUMMARY
echo "- Forge: ${forge_version}" >> $GITHUB_STEP_SUMMARY
