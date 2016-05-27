# image-search

Searches a mongo database for images with specified metadata

## Installation

Download from http://github.com/soulflyer/image-search

## Usage

Currently only useful from the repl so do:

lein repl

or fire up emacs and cider

## Commands

### eq lt le gt ge 
These all take the same form:

    (eq images meta-data-field value)
    
filters "images" selecting all where the contents of the metadata field match "value". Most of them only make sense with numbers. Numbers will be cleaned up, ie 1/100 will be read as 100 and any leading or trailing text will be removed. eq can also be used with strings, although the in might be a better choice.

### in

    (in images metadata-field value)
    
This will filter images returning all where metadata-field contains value. If metadata-field contains a string, then it will amtch if value is a substring of the metadata string. This is a case INsensitive match.
If metadate-field is a collection then an exact case sensitive match to one of the members is needed.

### open

This will open all the images in the list. The size of image is selected with the second parameter, thumbnail medium large or fullsize. The paths for these are all looked up in the preferences collection in the database.

    (open images medium)
    
## Examples

    (-> all-images
        (eq :ISO-Speed-Ratings 640)
        (eq :Exposure-Time 160)
        (open medium))
        
## License

Copyright © 2016 Iain Wood

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
