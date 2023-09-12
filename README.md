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



### Show constraint at a template path

```shell
$ gradle run --args="constraint ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-SECTION.test_all_types.v1]/items[at0001]/items[at0002]/items[archetype_id=openEHR-EHR-ACTION.test_all_types.v1]/ism_transition/careflow_step/defining_code"
```

```shell
$ gradle run --args="constraint ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0013]/value"
```

### Data validation

NOTE 1: for data validation the given path should correspond to a data value, usualy a path that constraints an ELEMENT.value attribute in the archetype.

NOTE 2: since a data set should be given, and each data value has different attributes, each data valu has it's own format for providing data:

- DV_QUANTITY: `magnitude|units` (magnitude is a number, units is a string)
- DV_ORDINAL: `value` or `terminology::code` or `value|terminology::code` (code is a string, value is a number, terminology is a string)
- CODE_PHRASE: `terminology::code` (code and terminology are both strings)
- DV_TEXT: `text` (text is a string, should be quoted if it has spaces)

NOTE 3: a template path could point to a node that has alternative data types, so the input data type should have the format of the type that is tried to be matched.

```shell
$ gradle run --args="validate ../../openEHR-OPT/src/main/resources/opts/test_all_types_en_v1.opt /content[archetype_id=openEHR-EHR-OBSERVATION.test_all_types.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value 1000|mg"
```
