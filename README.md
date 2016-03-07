# image-search

Searches a mongo database for images with specified metadata

## Installation

Download from http://github.com/soulflyer/image-search

## Usage

Currently only useful from the repl so do:

lein repl

or fire up emacs and cider

## Examples

    (-> all-images
        (eq :ISO-Speed-Ratings 640)
        (eq :Exposure-Time 160))
        
## License

Copyright © 2016 Iain Wood

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
