# NubiaParts

To get started, clone this repository to `packages/apps/NubiaParts` then inherit the config makefile in your `device.mk`.

```
$(call inherit-product, packages/apps/NubiaParts/config.mk)
```

To include the Fan Control tile, build `NubiaFanControl`.
```
PRODUCT_PACKAGES += NubiaFanControl
```
To include the tile for Game Keys/Shoulder buttons, build `NubiaGameKeys`.
```
PRODUCT_PACKAGES += NubiaGameKeys
```