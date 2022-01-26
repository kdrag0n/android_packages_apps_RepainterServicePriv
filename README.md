# Repainter ROM integration

This service provides first-class custom ROM integration for my [Repainter](https://repainter.kdrag0n.dev) app, which offers customizable dynamic theming for Android 12.

## Benefits

- Convenient for users (no prompts necessary)
- Faster and more reliable than root and Shizuku backends
- Full functionality with all customization (no limitations), even on the January security patch
- ROM-specific configuration: dynamic boot animation colors, etc.

## Features

- Versioned API
- Security: permission guarded by client signature
- Supports Direct Boot
- Automatically clean up themes on uninstall
- Link to app website in Settings -> App details -> Repainter Integration Service for users to see what the service is for (no launcher icon)

## Adding to a ROM

### 1. Add this repository

Clone this repository to `packages/apps/RepainterServicePriv`.

### 2. Configure the service

This step is optional, but **highly encouraged** for better integration.

The easiest way to configure the Repainter service is by forking the repository and editing [res/values/config.xml](res/values/config.xml). The config is documented with examples and comments, so configuration should be trivial.

You can also make a copy of config.xml and add it to an overlay package in `PRODUCT_PACKAGE_OVERLAYS`, such as `vendor/proton/overlay/packages/apps/RepainterServicePriv/res/values/config.xml`, and edit the overlay copy instead.

[Example commit](https://github.com/ProtonAOSP/android_vendor_proton/commit/1cd312d3e57d4273199d1ed3fc0f6b29af04e577)

### 3. Build the service

Add `RepainterServicePriv` to `PRODUCT_PACKAGES` somewhere in order to build the service:

```makefile
# Repainter integration
PRODUCT_PACKAGES += \
    RepainterServicePriv \
```

[Example commit](https://github.com/ProtonAOSP/android_vendor_proton/commit/c195b5b76e85b4860e990a5549079d6ac91dc696)
