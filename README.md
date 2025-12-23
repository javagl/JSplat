# JSplat

Java libraries for Gaussian splats.

<p align="center">
<img src="screenshot/screenshot.png" />
</p>


## Overview

### Libraries

- [`jsplat`](./jsplat) - The core library, with interfaces for splats and readers and writers
- Reader/writer libraries:
  - [`jsplat-io-gsplat`](./jsplat-io-gsplat) - [gsplat](https://github.com/antimatter15/splat) format
  - [`jsplat-io-ply`](./jsplat-io-ply) - PLY files
  - [`jsplat-io-spz`](./jsplat-io-spz) - [SPZ](https://github.com/nianticlabs/spz) format
  - [`jsplat-io-gltf`](./jsplat-io-gltf) - glTF with [`KHR_gaussian_splatting`](https://github.com/KhronosGroup/glTF/pull/2490)
  - [`jsplat-io-gltf-spz`](./jsplat-io-gltf-spz) - glTF with [`KHR_spz_gaussian_splatting_compression_spz_2`](https://github.com/KhronosGroup/glTF/pull/2531)
  - [`jsplat-io-sog`](./jsplat-io-sog) - [SOG Format](https://developer.playcanvas.com/user-manual/gaussian-splatting/formats/sog/), with a basic reader and an **experimental** (and slow) writer.

A basic viewer implementation can be found in [`jsplat-viewer`](./jsplat-viewer),
with the actual implementation based on LWJGL in [`jsplat-viewer-lwjgl`](./jsplat-viewer-lwjgl).

A basic application that brings together all JSplat libraries can be found
in [`jsplat-app`](./jsplat-app).

The viewer and application are experimental. In order to view existing splat
files, I recommend [babylon.js](https://sandbox.babylonjs.com/): It supports 
most of the formats that are commonly used, and can conveniently display them 
via drag-and-drop.

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


