/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

/**
 * An enumeration of the splat file formats supported by JSplat
 */
enum SplatFormat
{
    /**
     * The gsplat.js format  
     */
    GSPLAT, 
    
    /**
     * PLY format, ASCII
     */
    PLY_ASCII, 
    
    /**
     * PLY format, binary, little endian
     */
    PLY_BINARY_LE, 
    
    /**
     * PLY format, binary, big endian
     */
    PLY_BINARY_BE, 
    
    /**
     * SPZ format
     */
    SPZ, 
    
    /**
     * glTF format (without compression), stored as binary glTF
     */
    GLTF,
    
    /**
     * glTF format, with SPZ compression, stored as binary glTF
     */
    GLTF_SPZ,
    
    /**
     * The playcanvas SOG format
     */
    SOG
    
}