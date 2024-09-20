# Bouncer

[![Clojars Project](https://img.shields.io/clojars/v/com.wallbrew/bouncer.svg)](https://clojars.org/com.wallbrew/bouncer)
[![GitHub](https://img.shields.io/github/license/Wall-Brew-Co/bouncer)](https://github.com/Wall-Brew-Co/bouncer/blob/master/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/WallBrew?style=social)](https://twitter.com/WallBrew)

A Wall Brew [Leiningen](https://leiningen.org/) plugin for managing Wall Brew Leiningen projects.

## Rationale

The [Wall Brew Open Source Standard](https://github.com/Wall-Brew-Co/open-source?tab=readme-ov-file#automating-opinions) encourages development standards to be automated wherever possible.
For Wall Brew projects, that often relies on a collection of Leiningen plugins and consistency across project.clj files.
To automate the consistency checks, Bouncer is able to:

* Inspect the [licensing information](https://github.com/Wall-Brew-Co/open-source?tab=readme-ov-file#licensing)
* Inspect the Leiningen plugins used for our standard [CI/CD jobs](https://github.com/Wall-Brew-Co/rebroadcast/tree/master/sources/github-actions)

## Installation

Bouncer is available as a leiningen plugin and can be downloaded from [clojars](https://clojars.org/com.wallbrew/bouncer).
To install Bouncer, add the following in your `:plugins` list in your `project.clj` file:

```clj
[com.wallbrew/bouncer "1.0.0"]
```

The first time you invoke this plugin, Leiningen will automatically fetch the dependency for you.
Wall Brew projects should come with this dependency pre-installed.
Non-Wall Brew projects are free to use this plugin if they want to follow our development practices as well.

## Usage

From the root of your project directory, you may invoke the following commands:

* `init` - To initialize Nouncer with a config file (if not present)
* `check` - To validate Bouncer's rules against the current Leiningen project
* `help` - To view the help text of Bouncer or any command

Commands may accept several options, which can be configured by the command line arguments passed in or by a configuration file located at `.bouncer/config.edn`.
In all cases, the options will follow this order of precedence:

1. Arguments passed by command line
2. Values stored in `.bouncer/config.edn`
3. Default values in bouncer's implementation

### Initialize Bouncer

Bouncer stores its configuration at `.bouncer/config.edn`.
Since new projects will not have this directory by default, Bouncer may create it for you.

```sh
$ lein bouncer init
No configuration file found. Assuming default configuration.
Writing to .bouncer/config.edn
$ ls .bouncer -l
total 4
-rw-r--r-- 1 nnichols nnichols 1002 Sep 20 10:08 config.edn
```

If the `.bouncer` directory already exists, bouncer will perform no actions.

```sh
$ lein bouncer init
Reading from .bouncer/config.edn
Existing Bouncer configuration found.
```


### Check A Project With Bouncer

Bouncer's configuration contains a collection of rules to run against a project.
While rules may support additional data for configuration, each rule must support the following keys:

* `:comment` - A human readable string describing the rule's purpose or intent.
* `:disabled` - A boolean indicating if the rule is disabled. If the key is not present, the rule is assumed to be enabled.
* `:reason` - A human readable string describing why a particular rule has been disabled. If `:disabled` is true, this value must be present.

If bouncer successfully validates all rules, it will print information about the checks it has performed and exit with a 0 status code.

```sh
$ lein bouncer check
Reading from .bouncer/config.edn
Checking project.clj against Wall Brew standards...
PASS: License information is valid.
WARN: Some plugins rules are disabled. Skipping...
PASS: Plugins information is valid.
PASS: Project conforms to all rules.
```

If bouncer cannot validate a rule, it will print one or more failure messages and exit with a 1 status code.

```sh
$ lein bouncer check
Reading from .bouncer/config.edn
Checking project.clj against Wall Brew standards...
PASS: License information is valid.
FAIL: One or plugins not installed: ("lein-cljsbuild")
FAIL: Project does not conform to one or more rules.
```

Bouncer currently validates the following rules:

#### Licensing Information

- Rule Path `[:project-rules :license]`

Verifies that the root `project.clj` contains the following `:license` block (or one equivalent to it):

```clj
{:name         "MIT"
 :url          "https://opensource.org/licenses/MIT"
 :distribution :repo
 :comments     "Same-as all Wall-Brew projects"}
```

This renders the license information in the project's `pom.xml`, which is automatically scanned by license checking tools such as [Snyk](https://docs.snyk.io/).

#### Leiningen Plugins

- Rule Path `[:project-rules :plugins]`

Verifies that the root `project.clj` contains the following `plugins`.
This list is not exclusive, and a project may define additional tools; however, these tools are rquired by our GitHub Actions.

- `com.wallbrew/lein-sealog` - The default [changelog management tool](https://github.com/Wall-Brew-Co/lein-sealog)
- `com.github.clj-kondo/lein-clj-kondo` - The default [linter plugin](https://github.com/clj-kondo/lein-clj-kondo)
- `mvxcvi/cljstyle` - The default [code formatter](https://github.com/greglook/cljstyle)
- `lein-cljsbuild` - The default [Clojurescript build tool](https://clojars.org/lein-cljsbuild)

Individual plugin checks may set a `:disabled` key to opt-out on unrequired plugins.
For example, [clj-xml](https://github.com/Wall-Brew-Co/clj-xml) would opt-out of the `lein-cljsbuild` plugin since it is only available for JVM-based Clojure.
Like all higher-level rules, disabled plugins must also provide a `:reason`.

## License

Copyright © [Wall Brew Co](https://wallbrew.com/)

This software is provided for free, public use as outlined in the [MIT License](https://github.com/Wall-Brew-Co/bouncer/blob/master/LICENSE)
