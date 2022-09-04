![](docs/img/iCP7g1E.png)
___

# ahd-upload-checker

> ⚠️ This project was developed in 2009 and is no longer maintained ⚠️

![](https://img.shields.io/badge/java-1.5-blue)

`AHD Upload Checker` (or just `Upload Checker`) is a program written in java
with a java swing interface which automatically checks a video file for DXVA and
Awesome compliance for Awesome-HD tracker.

Compliance is indicated by outputting on the java swing the results
`Not Good Enough`, `Good`, `Discouraged`.

## Usage

If you want to execute the pre-compiled version you can do it by issuing the
following commands:

```shell
make create-release
cd release
java -jar UploadChecker.jar
```

For more information on the project refer to
[here](release_template/README.markdown).

## License

    AHD Upload Checker - Simple Java Swing application to check encodes
    for DXVA and Awesome compliancy based on their MediaInfo output.
    Copyright (C) 2009  Tiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

For the full license text refer to [LICENSE.md](LICENSE.md)
