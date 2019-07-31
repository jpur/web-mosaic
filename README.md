# web-mosaic

A web app for transforming images into mosaics (a composition of the image given many smaller images), built using Spring Boot.

![0205e212d57d46e5af326f3b3135-1450227](https://user-images.githubusercontent.com/9266693/60659745-7c0e2f00-9e99-11e9-8632-d343939e7c07.jpg) ![1](https://user-images.githubusercontent.com/9266693/60659748-7dd7f280-9e99-11e9-8496-a9b4e7aeada0.png)

# Configuration
You need to supply the app with sub-images which are used to transform an image into a mosaic. For the example in this README, the CIFAR-10 dataset, resized to 10x10, was used. You'll also want to write a script to generate a JSON file where each entry contains the file name and its average color. An example JSON file would look like:

```
[{"name": "0_cat.png", "color": {"r": 110, "g": 110, "b": 104}}, 
{"name": "1000_dog.png", "color": {"r": 138, "g": 132, "b": 128}}]
```

Make sure to update the locations in *src/main/resources/application.properties* and other relevant properties before running the app.
