# Build Scripts

## build-pages.sh

Builds the GitHub Pages site using Jekyll from the bundled content.

### Usage

```bash
# First, build and bundle all content
./gradlew bundleRelease

# Then build the Jekyll site
./scripts/build-pages.sh [output_directory]
```

### Parameters

- `output_directory` (optional): Where to output the built site. Defaults to `build/site`

### What it does

1. Checks that `build/bundleRelease` exists (created by `./gradlew bundleRelease`)
2. Verifies Jekyll is installed
3. Runs Jekyll build with `build/bundleRelease` as source
4. Outputs the final HTML site to the specified directory

### Requirements

- Jekyll installed (`gem install jekyll bundler`)
- Bundled content at `build/bundleRelease` (run `./gradlew bundleRelease` first)

### Example

```bash
# Complete build process
cd samples/online-parser
./gradlew bundleRelease

cd ../..
./gradlew bundleRelease
./scripts/build-pages.sh

# Site is now at build/site
```

## Build Architecture

The Pages build is split into two phases:

### Phase 1: Content Bundling (`./gradlew bundleRelease`)

Collects all content into `build/bundleRelease`:
- `pages/` directory (Jekyll config, layouts, includes, assets, docs)
- `samples/online-parser/build/site/` (JS output + social image)
- `build/dokka/` (KDoc API documentation)
- `index.md` (generated from README.md)

### Phase 2: Jekyll Build (`./scripts/build-pages.sh`)

Processes the bundled content with Jekyll to generate the final HTML site.

This separation provides:
- **Cleaner workflow**: Simple, two-step build process
- **Better caching**: Content bundling can be cached separately
- **Local testing**: Easy to build and preview locally
- **Maintainability**: Clear separation of concerns
