[![Build Status](https://dev.azure.com/pt-iot/smartsite/_apis/build/status/csm.cloud.oauth-utilities?branchName=master)](https://dev.azure.com/pt-iot/smartsite/_build/latest?definitionId=56?branchName=master)

# Introduction

This repo contains code for oauth utilities of the smartsite backend services.

# Build and Versioning

The project is build using the [gradle build tool](https://gradle.org/). You can trigger a build using this command:

```Bash
./gradlew clean build
```

The version numbers follow the pattern **MAJOR.MINOR.PATCH**.
Increment the:

- *MAJOR* version when you make incompatible API changes,
- *MINOR* version when you add functionality in a backwards-compatible manner, and
- *PATCH* version when you make backwards-compatible bug fixes.

The target version of this library is calculated using special messages of all git commits between the last git tag up to the git head reference.

| Semantic version | Sample  | Commit message prefix  |
| -----------------|:-------:| ----------------------:|
| Patch            | 1.0.1   | SMAR-1234: xxx         |
| Patch            | 1.0.1   | SMAR-1234: xxx         |
| Minor            | 1.1.0   | SMAR-1234: xxx         |
| Major            | 2.0.0   | SMAR-1234: xxx         |

# Release

As long as you implement new features or fixes to this library you are working on *SNAPSHOT* versions (like 1.0.0-SNAPSHOT).
If you want to release the library this should be done in two steps:

1. Create a tagged version without the *SNAPSHOT* suffix (e.g. **1.0.0**)
2. Add a new commit to the repo with switching to next *SNAPSHOT* version (e.g. 1.0.1-SNAPSHOT) to continue next implementation.

These steps are done using the [gradle release plugin](https://github.com/researchgate/gradle-release).

To create a new release first all changes have to be commited and pushed to the remote repo.
Then you can create the release by using the following command:

```Bash
./gradlew release
```

This will work in interactive mode by asking to confirm the release version and the next *SNAPSHOT* version.

You can also use this command in automatic mode (i.e. without any interaction):

```Bash
./gradlew release -Prelease.useAutomaticVersion=true
```

**Please note:** The plugin creates and pushes two commits, one for tagging the release and another one for switching back to next *SNAPSHOT* version.
