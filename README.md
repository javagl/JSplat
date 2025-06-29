# JSplat

Java libraries for Gaussian splats.

<p align="center">
<img src="./screenshot/screenshot.png" />
</p>


## Overview

No, there is no viewer (yet). I recommend [babylon.js](https://sandbox.babylonjs.com/) for
viewing splat files. It supports most of the formats that are commonly used, and can
conveniently display them via drag-and-drop.

### Libraries

- [`jsplat`](./jsplat) - The core library, with interfaces for splats and readers and writers
- Reader/writer libraries:
  - [`jsplat-io-gsplat`](./jsplat-io-gsplat) - [gsplat](https://github.com/antimatter15/splat) format
  - [`jsplat-io-ply`](./jsplat-io-ply) - PLY files
  - [`jsplat-io-spz`](./jsplat-io-spz) - [SPZ](https://github.com/nianticlabs/spz) format
  - [`jsplat-io-spz-gltf`](./jsplat-io-spz-gltf) - glTF with [`KHR_spz_gaussian_splats_compression`](https://github.com/KhronosGroup/glTF/pull/2490)


### Examples

An example

```java
// Create an input stream for the PLY data
InputStream is = new FileInputStream("./data/unitCube-ascii.ply");

// Create a PLY reader, and read the splats
SplatListReader r = new PlySplatReader();
List<MutableSplat> splats = r.readList(is);

// Create an output stream for the SPZ data
OutputStream os = new FileOutputStream("./data/unitCube-output.spz");

// Create an SPZ writer, and write the splats
SplatListWriter w = new SpzSplatWriter();
w.writeList(splats, os);
```

Further examples can be found in the [examples](./jsplat-examples/src/main/java/de/javagl/jsplat/examples) project.


