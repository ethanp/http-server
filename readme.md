### But what does it DO?

You can navigate your local file tree, and look at files.

Life begins at the root of this directory. I.e. in the directory that this `readme` sits in.
From there you can navigate around.


#### Sample usage

1. Open browser
2. Navigate to `localhost:8080`
    * Now you should see a listing of the files in _this_ directory
3. Click on a file
    * If it's a directory, it shall list that directory
    * If it's doesn't exist, you shall be told so
    * If it's a file, that file shall be rendered to the browser as plain text


### TODO

1. Send over the favicon.png on requests for `/favicon.ico`...not sure the right format for that.
2. Add one of those snazzy .prop files that has logging options like
    1. log request text
    2. log response text
    3. log request headers
    4. log response headers
