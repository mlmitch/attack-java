# attack-java

A Java library for working with MITRE ATT&CK®.

## Usage

Update here with maven instructions and some example code once non snapshot release is cut and available in maven central.

## Development

### Generation

First, ensure this repository is checked out with submodules initialized.
If needed, update the MITRE CTI submodule.
Additionally, you may want to delete the existing XML file(s) under `data/src/main/resources/`.

```shell
attack-java$ python3 -m venv generation/venv
attack-java$ source generation/venv/bin/activate
attack-java$ pip install -r generation/requirements.txt
attack-java$ python generation/main.py
```

### Data Artifact

```shell
attack-java/data$ mvn clean install
```

### Library Artifact

First, ensure the data artifact is installed with `pom.xml` versions set correctly.

```shell
attack-java/lib$ mvn clean install
```

## License

This repository is seperated into `generation`, `data` and `lib` subfolders.

The `generation` subfolder contains the code to generate XML files from [MITRE's CTI repository](https://github.com/mitre/cti/).
This code is available under the [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
However, it is unlikely that you'll use this code as it isn't distributed with attack-java.

The `data` subfolder contains the project where the generated XML files land.
These XML files are a modified copy of [MITRE's CTI data](https://github.com/mitre/cti/).
Therefore, this data is available under [MITRE's CTI License](https://github.com/mitre/cti/blob/master/LICENSE.txt).

Finally, the `lib` subfolder contains the attack-java library code.
This code is available under the [Apache-2.0 license](https://www.apache.org/licenses/LICENSE-2.0).

The Java artifacts produced from the `data` and `lib` subfolders are distributed separately in maven in order to make this licensing clear.
However, you only need to depend on the `com.wassonlabs.attack-java` artifact to use the library, because a dependency is already established between the two artifacts.

Please see the LICENSE file in each subfolder for more information.
