# edu-openehr-templates

openEHR templates in software: examples for CaboLabs' training courses

## Build

```shell
$ gradle build
$ gradle fatJar
```

## Run from Gradle

NOTE: when running with gradle, the current working directory is ./app not ./ so the path to archetype needs an extra ../ if using relative paths.

### Try parse an archetype (returns an error if the OPT is not correct)

```shell
$ gradle run --args="parse ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt"
```