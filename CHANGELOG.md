# Changelog

## [0.2.0](https://github.com/GeraldineRouy/step-by-step/compare/step-by-step-v0.1.0...step-by-step-v0.2.0) (2026-11-01)


### Features

* **app:** add a rest api over the task business rules ([a6cfc03](https://github.com/GeraldineRouy/step-by-step/commit/a6cfc03014e16e354f8bc39825526e8532f1843b))
* **app:** add a rest api over the task business rules ([a6cfc03](https://github.com/GeraldineRouy/step-by-step/commit/a6cfc03014e16e354f8bc39825526e8532f1843b))
* **app:** add a rest api over the task business rules ([27bd0e2](https://github.com/GeraldineRouy/step-by-step/commit/27bd0e22687dfce5c8553ce11a6b6a6ba0fbff38))
* **app:** add task domain model and postgres test stack ([0498e35](https://github.com/GeraldineRouy/step-by-step/commit/0498e358c46b60801b921b76e4d6b85765e7c66b))
* **app:** add task domain model and postgres test stack ([fae5c5f](https://github.com/GeraldineRouy/step-by-step/commit/fae5c5f8e403774bbb187d656441c51a2024635f))
* **cd:** deploy to staging, approve, then blue/green production with rollback ([352c1ca](https://github.com/GeraldineRouy/step-by-step/commit/352c1ca8574be1aaa7d71ead56233f27c33e0e7a))
* **cd:** deploy to staging, approve, then blue/green production with… ([7eef8cf](https://github.com/GeraldineRouy/step-by-step/commit/7eef8cf21874780fc86e3d1ad30612162b8ca5d5))
* **ci:** extract common steps into the step-by-step-lib shared library ([160f129](https://github.com/GeraldineRouy/step-by-step/commit/160f1292110fcb121622f68b6c7ca585bcdd6ed5))
* **ci:** extract common steps into the step-by-step-lib shared library ([160f129](https://github.com/GeraldineRouy/step-by-step/commit/160f1292110fcb121622f68b6c7ca585bcdd6ed5))
* **ci:** extract common steps into the step-by-step-lib shared library ([ef9e153](https://github.com/GeraldineRouy/step-by-step/commit/ef9e153a2f8fb4d10c6000e37bbe110554e73918))
* **ci:** read the artefact version from pom.xml (step-by-step-lib 1.… ([dd4b01b](https://github.com/GeraldineRouy/step-by-step/commit/dd4b01b3656724a10a784dd200b24a7dff63ba22))
* **ci:** read the artefact version from pom.xml (step-by-step-lib 1.1.0) ([2826ec3](https://github.com/GeraldineRouy/step-by-step/commit/2826ec36e86d6022f7e8f94cc1b41e77f07b2d9d))
* **release:** configure automated releases with release-please ([9c04569](https://github.com/GeraldineRouy/step-by-step/commit/9c04569bc73d4734637c34244bbfbedbaa933c9b))
* **release:** configure automated releases with release-please ([f129d35](https://github.com/GeraldineRouy/step-by-step/commit/f129d359b921b59339edcf1d9aed3179d215d906))
* **staging:** add idempotent staging deployment ([0321da6](https://github.com/GeraldineRouy/step-by-step/commit/0321da611290e3b2795d2463dada69b14040f64f))
* **staging:** add idempotent staging deployment ([fd9bdda](https://github.com/GeraldineRouy/step-by-step/commit/fd9bddace64079516a8e5756707f53931e61cc3a))


### Bug Fixes

* **build:** remove duplicated failsafe plugin declaration ([9ddcc03](https://github.com/GeraldineRouy/step-by-step/commit/9ddcc033cf5815619c49f487e14ae023d67f49b4))
* **build:** use checkstyle 12, the last line running on java 17 ([09ca409](https://github.com/GeraldineRouy/step-by-step/commit/09ca4092255b483296e4abbb8dd952bc0f315001))
* **ci:** give each build's test database a free port (step-by-step-lib 1.1.1) ([dd4b01b](https://github.com/GeraldineRouy/step-by-step/commit/dd4b01b3656724a10a784dd200b24a7dff63ba22))
* **ci:** give each build's test database a free port (step-by-step-lib 1.1.1) ([30a5da2](https://github.com/GeraldineRouy/step-by-step/commit/30a5da20b76f36b029fc91e6c53469e51efeb9e1))
* **pipeline:** keep the test database name under the 63-character dns limit ([b8cbf45](https://github.com/GeraldineRouy/step-by-step/commit/b8cbf451f2577e4b07f412d1604be4223b2b8fda))
* **pipeline:** publish to nexus with curl like the story 004 witness ([ac12142](https://github.com/GeraldineRouy/step-by-step/commit/ac121422cd9f04cceafa126c3fb9d87c5d1edc7f))
* **pipeline:** publish to nexus with curl like the story 004 witness ([17a2f6f](https://github.com/GeraldineRouy/step-by-step/commit/17a2f6f71a6e59aaa01ed5a1c67d78ee8a809709))
* **repo:** skip the maven wrapper script in the secret assignment scan ([ae35d31](https://github.com/GeraldineRouy/step-by-step/commit/ae35d31bb607a77d1c82eceaa1fd05484b915989))


### Documentation

* add specification, user stories and tutorials ([5d6a8c9](https://github.com/GeraldineRouy/step-by-step/commit/5d6a8c94f1a271e4a0369b1a57c5c6a8defe9c25))
* **app:** add story 007 tutorials and record spring boot decisions ([cac8211](https://github.com/GeraldineRouy/step-by-step/commit/cac8211aba1f7c0d1df7840c8a9d63f900fe6920))
* **app:** add story 008 tutorials and record data model decisions ([d545ccc](https://github.com/GeraldineRouy/step-by-step/commit/d545ccc12fb7333c5c91fa7f95b614fdb1da4a89))
* **app:** add story 008 tutorials and record data model decisions ([f5b6f3e](https://github.com/GeraldineRouy/step-by-step/commit/f5b6f3e7eb258d0c26dcc250acf22ccdf5241b73))
* **app:** add story 009 tutorials ([ffa5f36](https://github.com/GeraldineRouy/step-by-step/commit/ffa5f361db1be60628d095f9b9f569cc68f10c09))
* **app:** record story 007 tutorial feedback ([786e307](https://github.com/GeraldineRouy/step-by-step/commit/786e30782ca04c7e748a4e1bf24593482c66a86f))
* **jenkins:** explain webhook delay and add quiet period step ([92eaafd](https://github.com/GeraldineRouy/step-by-step/commit/92eaafdfe85553b7326034385e33a6e43f0935ac))
* **jenkins:** fill in measured webhook delay ([7e21d75](https://github.com/GeraldineRouy/step-by-step/commit/7e21d75f2013ee03d882522488373c4f3f5f5b1f))
* **jenkins:** record webhook push-to-build delay ([ba79c8b](https://github.com/GeraldineRouy/step-by-step/commit/ba79c8bc72b4e7510c4064a4c504f0b341fa2984))
* **report:** add technical report, demo script and api decisions ([cd9b870](https://github.com/GeraldineRouy/step-by-step/commit/cd9b8702712b6dd8b377e8153361e6fe895ee403))
* **team:** add contributing guide and onboarding tour ([3ce116e](https://github.com/GeraldineRouy/step-by-step/commit/3ce116ed0a773d85abcd7ad084d0e51345f0f2ee))
* **team:** add contributing guide and onboarding tour ([f040279](https://github.com/GeraldineRouy/step-by-step/commit/f04027978088c29abc43cb95d94d78762214f732))
* **team:** add first-run notes from onboarding ([b026d97](https://github.com/GeraldineRouy/step-by-step/commit/b026d9715b94ae69badb16fb88fd74350349bcfc))
* **team:** add first-run notes from onboarding ([94b9d74](https://github.com/GeraldineRouy/step-by-step/commit/94b9d74bb43293b0c3a5618bc2045f3064ef01b2))
* **team:** record ruleset limitation on private repository ([79bfba5](https://github.com/GeraldineRouy/step-by-step/commit/79bfba594ae98b967e1d950fa08c6b3523081bab))
* **team:** switch to a two-person team and a recorded demo ([4aec635](https://github.com/GeraldineRouy/step-by-step/commit/4aec6356b95add47ce78c88b7163cfdd53048d42))
