# image-search

Searches a mongo database for images with specified metadata. Certain assumptions are made about the files containing the pics:

The pictures are stored in various sizes. In each size, the last part of the pathname is of the form: yyyy/mm/dd-project-name

The mongo database contains collections for the images, the keywords and the preferences. The keyword collection is a heirarchy, and searching for a keyword using image search will search for images containing that keyword or any of its sub keywords.

Check out my other projects for more info on creating and maintaining the database. Image-search is basically a wrapper for the [image-lib project](http://github.com/soulflyer/image-lib)

## Installation

Download from <http://github.com/soulflyer/image-search>

## Usage

### Command line

image-search can be called from the command line for simple queries. I build it with `lein bin` which creates the executable and moves it to `~/bin/image-search`. It can also be built using `lein uberjar` and run with `java -jar target/image-search-0.1.0-standalone.jar` or something similar.

This one will return a count of all images taken in 2015 with an ISO of 640

    image-search -c -y 2015 -i 640
    
To see all the available options do this:

    image-search -h

### Repl 

For more complex queries use find-images as a clojure library .The files play.clj and search.clj are good starting points.

## Setup

There is a collection of preferences in the database. Most important are the paths to the various sizes of images, and the path to the executable for opening the files. These should be set to the correct values for your own system. Default file opener is /usr/bin/open which is fine for OSX. 

## Commands

### eq lt le gt ge 
These all take the same form:

    (eq images meta-data-field value)
    
filters "images" selecting all where the contents of the metadata field match "value". Most of them only make sense with numbers. Numbers will be cleaned up, ie 1/100 will be read as 100 and any leading or trailing text will be removed. eq can also be used with strings, although the in function might be a better choice.

### in

    (in images metadata-field value)
    
This will filter images returning all where metadata-field contains value. If metadata-field contains a string, then it will match if value is a substring of the metadata string. This is a case insensitive match.
If metadate-field is a collection then an exact case sensitive match to one of the members is needed.

### open

This will open all the images in the list. The size of image is selected with the second parameter, thumbnail medium large or fullsize. The paths for these are all looked up in the preferences collection in the database. 

    (open images medium)
    
## images

images-> and images are shorthand for -> all-images. These are the same query:

    (-> all-images
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))

    (images->
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))

    (images
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))
      
## or and

Chaining the forms as above is effectively doing an and. We can also do an or like this:

    (images
      (or
        (eq :ISO-Speed-Ratings 640)
        (eq :Exposure-Time 160)))
        
Because or expects a series of forms, we can no longer chain them to get an assumed and. If we need to nest an and inside an or, it must be done specifically, like this:

    (images
      (or
        (eq :ISO-Speed-Ratings 640)
        (and
          (eq :Exposure-Time 160)
          (in :Model "phone")))
      (open medium))

## paths

Returns only the path of each image in the collection

## write

Outputs the collection to a file. It doesn't delete the contents if the file already exists.

    (images
      (in :Model "phone")
      (paths)
      (write "/tmp/phone-pics"))
      
## Examples

    (images
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))
        
Take a look in play.clj for further examples.

## TODO

Add an option to the command line version to allow selecting by rating or keyword.

Add the ability to search for an incomplete keyword (or offer completions).

## License

Copyright © 2016-2023 Iain Wood
