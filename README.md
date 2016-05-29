# image-search

Searches a mongo database for images with specified metadata. Certain assumptions are made about the files containing the pics:

The pictures are stored in various sizes. In each size, the last part of the pathname is of the form: yyyy/mm/dd-project-name

There is a directory for each of thumbnail, medium, large and fullsize pictures. These directories are specified in the preferences collection in the database.

The database is mongo. It contains collections for the images, the keywords and the preferences. The keyword collection is a heirarchy, and searching for a keyword using image search will search for images containing that keyword or any of its sub keywords.

Check out my other projects for more info on creating and maintaining the database.

## Installation

Download from http://github.com/soulflyer/image-search

## Usage

Currently only useful from the repl so do:

lein repl

or fire up emacs and cider

## Setup

There is a collection of preferences in the database. Most important are the paths to the various sizes of images, and the path to the executable for opening the files. These should be set to the correct values for your own system. Default file opener is /usr/bin/open which is fine for OSX. 

## Commands

### eq lt le gt ge 
These all take the same form:

    (eq images meta-data-field value)
    
filters "images" selecting all where the contents of the metadata field match "value". Most of them only make sense with numbers. Numbers will be cleaned up, ie 1/100 will be read as 100 and any leading or trailing text will be removed. eq can also be used with strings, although the in might be a better choice.

### in

    (in images metadata-field value)
    
This will filter images returning all where metadata-field contains value. If metadata-field contains a string, then it will match if value is a substring of the metadata string. This is a case INsensitive match.
If metadate-field is a collection then an exact case sensitive match to one of the members is needed.

### open

This will open all the images in the list. The size of image is selected with the second parameter, thumbnail medium large or fullsize. The paths for these are all looked up in the preferences collection in the database. 

    (open images medium)
    
## find

find is shorthand for -> all-images. These are the same query:

    (-> all-images
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))

    (find
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))

## or and

Chaining the forms as above is effectively doing an and. We can also do an or like this:

    (find
      (or
        (eq :ISO-Speed-Ratings 640)
        (eq :Exposure-Time 160)))
        
Because or expects a series of forms, we can no longer chain them to get an assumed and. If we need to nest an and inside an or, it must be done specifically, like this:

    (find
      (or
        (eq :ISO-Speed-Ratings 640)
        (and
          (eq :Exposure-Time 160)
          (in :Model "phone")))
      (open medium))

                            
## Examples

    (find
      (eq :ISO-Speed-Ratings 640)
      (eq :Exposure-Time 160)
      (open medium))
        
Take a look in play.clj for further examples.
        
## License

Copyright © 2016 Iain Wood

Distributed under the Eclipse Public License version 1.0
