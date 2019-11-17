# Overview

This project contains general purpose annotations for
Jackson Data Processor, used on value and handler types.

Project is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

-----

## Usage - installation

### Gradle, Maven, Java package

All annotations are in Java package `com.fasterxml.jackson.annotation`.

To use annotations, you need to use Maven dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-annotations</artifactId>
  <version>${jackson-annotations-version}</version>
</dependency>
```

## Usage - coding

### Annotations for renaming properties

### Annotations for Ignoring properties

-----

## Support

-----

## Further reading

Project-specific documentation:

* [Full Listing of Jackson Annotations](../../wiki/Jackson-Annotations) details all available annotations.
* [Other documentation](../../wiki)

Usage:

* You can make Jackson 2 use Jackson 1 annotations with [jackson-legacy-introspector](https://github.com/Laures/jackson-legacy-introspector)

Related:

* [Databinding](https://github.com/FasterXML/jackson-databind) module has more documentation, since it is the main user of annotations.
In addition, here are other useful links:
* [Jackson Project Home](http://wiki.fasterxml.com/JacksonHome)
* [Annotation documentation at FasterXML Wiki](http://wiki.fasterxml.com/JacksonAnnotations) covers 1.x annotations as well as 2.0

