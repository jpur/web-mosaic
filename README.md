# web-mosaic

A web app for transforming images into mosaics (a composition of the image given many smaller images), built using Spring Boot.

![Square, diamond and hex mosaics](https://user-images.githubusercontent.com/9266693/62209865-194b7d00-b3de-11e9-844e-148f60ef6212.png)

# Configuration
You need to supply the app with sub-images which are used to transform an image into a mosaic. For the example in this README, the CIFAR-10 dataset, resized to 10x10, was used. You'll also want to write a script to generate a JSON file where each entry contains the file name and its average color. An example JSON file would look like:

```
[{"name": "0_cat.png", "color": {"r": 110, "g": 110, "b": 104}}, 
{"name": "1000_dog.png", "color": {"r": 138, "g": 132, "b": 128}}]
```

Make sure to update the locations in *src/main/resources/application.properties* and other relevant properties before running.
