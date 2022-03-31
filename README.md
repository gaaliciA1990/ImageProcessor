# Image Processor

Personal project for Software Architecture and Design for the MSCS program at Seattle University.</br>
A simple Image Processor API that accepts a byte array image and can transform the image by any combination of the following usinga stateless design: 
- Rotate 90 degrees left or right
- Rotate any degrees (still being worked on as the image is distorted out of the frame)
- Flip horizontally or vertically
- Add a grayscale filter
- Resize an image by width and/or height based on pixel size
- Create a 160x108 pixel thumbnail

More functionality to come

## Tools and Language
The program is written in Kotlin and is using the Ktor Framework with the Scrimage Library to perform image transformations. Due to the limitations with degree rotation, excluding 90 degree, a new library is being added in the future to handle this operation more easily. 


## API Documentation
Full API documentation for how to perform the actions by calling the API can be found here:
https://github.com/gaaliciA1990/ImageProcessor/blob/master/ImageProcessorAPIDocumentation
