# Changelog

## [0.3.0](https://github.com/Devops-is-fun/step-by-step/compare/step-by-step-v0.2.0...step-by-step-v0.3.0) (2026-11-27)


### Features

* **app:** plan a time, complete or delete steps, delete tasks ([b7693f2](https://github.com/Devops-is-fun/step-by-step/commit/b7693f2482dda049d6cb8c3622ff2839f25ec20f))
* **app:** plan a time, complete or delete steps, delete tasks ([0564f66](https://github.com/Devops-is-fun/step-by-step/commit/0564f66445926844b48fc09e9e656739eac2f8a9))
* **cd:** continuous deployment with flux (gitops) ([052a704](https://github.com/Devops-is-fun/step-by-step/commit/052a704a8857ce92dd3a87288a2b58a63f74133c))
* **cd:** continuous deployment with flux (gitops) ([264c1f3](https://github.com/Devops-is-fun/step-by-step/commit/264c1f33ca920d130c9672b0e416ff2e8194c39c))
* **front:** add custom Step by Step logo ([fcda3bf](https://github.com/Devops-is-fun/step-by-step/commit/fcda3bfb485c27cc8637a352d30a72050ff07e6c))
* **front:** add Vue task list and forms ([6cc1a91](https://github.com/Devops-is-fun/step-by-step/commit/6cc1a9183ec24bbd5815b357e3b370fef8d36173))
* **front:** integrate Vue frontend with API, tests and Docker ([0ae3f92](https://github.com/Devops-is-fun/step-by-step/commit/0ae3f922eedaf891b8dab398b0ecf9e3b12bc7ae))
* **front:** Vue frontend, automated tests and DockerFeat/front ([cccfac5](https://github.com/Devops-is-fun/step-by-step/commit/cccfac5fab40c3611fefa9b3ceb56942f7d97998))
* **k8s:** automate kind cluster and metrics-server setup ([d635d8d](https://github.com/Devops-is-fun/step-by-step/commit/d635d8d25086b554e06736f843a0258a037f86e1))
* **k8s:** automate kind cluster and metrics-server setup ([b28c7a5](https://github.com/Devops-is-fun/step-by-step/commit/b28c7a5e19a17865ffd73d8d42ae1e3a5127167b))
* **k8s:** autoscale the backend with an HPA and add a k6 load test ([b8eb66f](https://github.com/Devops-is-fun/step-by-step/commit/b8eb66f212f22a0e9baccd7ad8df15ba323fd057))
* **k8s:** autoscale the backend with an HPA and add a k6 load test ([cacd0c3](https://github.com/Devops-is-fun/step-by-step/commit/cacd0c3bdcc3fd549deb1acd8c893db935d3b7f2))
* **k8s:** configure kind ingress and automate nginx installation ([a2a8746](https://github.com/Devops-is-fun/step-by-step/commit/a2a87463f6221ce135740c87277c11eaddc4894d))
* **k8s:** configure kind ingress and automate nginx installation ([89c2734](https://github.com/Devops-is-fun/step-by-step/commit/89c2734a36def62ca6d79c54c07f0800045f3322))
* **k8s:** deploy the app and postgres on kubernetes ([57d6d5f](https://github.com/Devops-is-fun/step-by-step/commit/57d6d5fd3705fba4353b0b13c329ce394ec466b1))
* **monitoring:** deploy Prometheus and Grafana on kind ([389abc2](https://github.com/Devops-is-fun/step-by-step/commit/389abc2ea510e407e429f7374762ee7d05a62a7c))
* **monitoring:** deploy Prometheus and Grafana on kind ([204cdc6](https://github.com/Devops-is-fun/step-by-step/commit/204cdc6717d2d3bf64b66835e0570654c728d340))
* **observability:** add Grafana SRE golden signals dashboard ([7fb9cc8](https://github.com/Devops-is-fun/step-by-step/commit/7fb9cc8c28784120ef1344b11c7563d89cfa102f))
* **observability:** add Grafana SRE golden signals dashboard ([09b97c3](https://github.com/Devops-is-fun/step-by-step/commit/09b97c33a264d8831a876f2d9508abed3c5e60be))
* **observability:** add prometheus grafana and dora metrics dashboard ([9aa969e](https://github.com/Devops-is-fun/step-by-step/commit/9aa969ec194f74a8b6522be8e8f8e3ece20f6984))
* **observability:** add prometheus grafana and dora metrics dashboard ([9aa969e](https://github.com/Devops-is-fun/step-by-step/commit/9aa969ec194f74a8b6522be8e8f8e3ece20f6984))
* **observability:** add prometheus grafana and dora metrics dashboard ([9a499dc](https://github.com/Devops-is-fun/step-by-step/commit/9a499dca443fd9a4b6c30f57efcd5fc2a481786c))
* **observability:** centralize Kubernetes logs with Loki and Alloy ([0eb81c9](https://github.com/Devops-is-fun/step-by-step/commit/0eb81c95108d7b332676baccabf6d0a23ae25c64))
* **observability:** centralize Kubernetes logs with Loki and Alloy ([33bb70a](https://github.com/Devops-is-fun/step-by-step/commit/33bb70a90de487922cc4efe7d61a923a8e128433))
* **observability:** enable HTTP histograms and backend ServiceMonitor ([95cb5c4](https://github.com/Devops-is-fun/step-by-step/commit/95cb5c4e352632b32487cb975a1df928345a538a))
* **observability:** enable HTTP histograms and backend ServiceMonitor ([6f9b1d3](https://github.com/Devops-is-fun/step-by-step/commit/6f9b1d356c812b61fa9aacc157d801e5ef426f90))
* **observability:** search logs by severity with a level label ([16f7b55](https://github.com/Devops-is-fun/step-by-step/commit/16f7b551ee883bf12d12d86c0f17fb07c82b9f35))


### Bug Fixes

* **cd:** run kind on kubernetes 1.37 and check the ServiceMonitor CRD before installing flux ([adf5036](https://github.com/Devops-is-fun/step-by-step/commit/adf50361a5474f43220da70e383fdb5ce14511d2))
* **ci:** clean target and publish the exact jar of the build (step-by-step-lib 1.2.0) ([f38b1e1](https://github.com/Devops-is-fun/step-by-step/commit/f38b1e112c8eb0210644a1088e9f7d5293fc7cf1))
* **ci:** clean target and publish the exact jar of the build (step-by-step-lib 1.2.0) ([f38b1e1](https://github.com/Devops-is-fun/step-by-step/commit/f38b1e112c8eb0210644a1088e9f7d5293fc7cf1))
* **ci:** clean target and publish the exact jar of the build (step-by-step-lib 1.2.0) ([1ec1984](https://github.com/Devops-is-fun/step-by-step/commit/1ec19849d5986eb4d47770208c4a30bba2536dc1))
* **front:** non-root securityContext and same retry on deploy push for both workflows ([30dcc13](https://github.com/Devops-is-fun/step-by-step/commit/30dcc13b6c3deb20ebe456a63c84b0a4aa9a4d64))
* **front:** update the nginx base image and apply alpine security updates ([43a51ea](https://github.com/Devops-is-fun/step-by-step/commit/43a51eaad560411170da8526a2d3bbefde0cf13c))
* **k8s:** move the helm monitoring values out of k8s/ ([aebb1be](https://github.com/Devops-is-fun/step-by-step/commit/aebb1be18d226fefce5995e4537ed2b368ebcedc))
* **k8s:** move the helm monitoring values out of k8s/ ([420d895](https://github.com/Devops-is-fun/step-by-step/commit/420d895b0be23349bb5ec481f3d06937026d576f))
* **k8s:** move the kind config out of k8s/ and use it in setup-cluster.sh ([d97ea40](https://github.com/Devops-is-fun/step-by-step/commit/d97ea4056dc7c2bd05a3bb6eb5e5929b5aab0491))
* **k8s:** move the kind config out of k8s/ and use it in setup-cluster.sh ([f8ebc93](https://github.com/Devops-is-fun/step-by-step/commit/f8ebc93e38835fb56407d3fe5e9df480e9e8acb1))
* **k8s:** no lost request when a backend pod stops (preStop pause) ([88a74ac](https://github.com/Devops-is-fun/step-by-step/commit/88a74ace69144f65da4ae126ebc359b92f9cbb50))
* **k8s:** no lost request when a backend pod stops (preStop pause) ([5960900](https://github.com/Devops-is-fun/step-by-step/commit/5960900e79d69c803bfe0ae047d4600b224791a1))
* **observability:** reach the apps from prometheus, add time to restore, keep tooling ports local ([ee70b32](https://github.com/Devops-is-fun/step-by-step/commit/ee70b3278e8b19e5de636144be1932484b154269))
* **observability:** reach the apps from prometheus, add time to restore, keep tooling ports local ([ee70b32](https://github.com/Devops-is-fun/step-by-step/commit/ee70b3278e8b19e5de636144be1932484b154269))
* **observability:** reach the apps from prometheus, add time to restore, keep tooling ports local ([ba1e5c2](https://github.com/Devops-is-fun/step-by-step/commit/ba1e5c2501c8aec725b8d784af3f661adf29c4b9))


### Documentation

* add demo video link, commentary and final dora measurements ([#21](https://github.com/Devops-is-fun/step-by-step/issues/21)) ([c52c3fc](https://github.com/Devops-is-fun/step-by-step/commit/c52c3fc46980a8551a67fb4d84dee3a6aafcc25a))
* add readme with full local reproduction instructions ([#18](https://github.com/Devops-is-fun/step-by-step/issues/18)) ([41491fd](https://github.com/Devops-is-fun/step-by-step/commit/41491fdab5f49de8ddc18f56f88a765206e8f167))
* add the demo script for the final presentation ([91c1bb0](https://github.com/Devops-is-fun/step-by-step/commit/91c1bb054bb895286a409e15fc75799f2f449396))
* add the project report and the grafana dashboard guide ([6c3f78c](https://github.com/Devops-is-fun/step-by-step/commit/6c3f78c7f3c93d29c78e1187bde7a86d7799b26b))
* add the project report and the grafana dashboard guide ([96724f1](https://github.com/Devops-is-fun/step-by-step/commit/96724f108a3433d6e3f06995e221fc4793bc55d5))
* complete the video commentary and record requirement status at delivery ([#22](https://github.com/Devops-is-fun/step-by-step/issues/22)) ([9ff44bd](https://github.com/Devops-is-fun/step-by-step/commit/9ff44bd41aae3f2c7420aee547cd7e683bb439f5))
* demo with Loki logs, without tracing (out of scope after discussion with the teacher) ([d684982](https://github.com/Devops-is-fun/step-by-step/commit/d684982d399cf3bef66a0078aecdbe1a54bf4705))
* explain how to deploy on kubernetes ([f73365f](https://github.com/Devops-is-fun/step-by-step/commit/f73365f00699310055ac30555a7b23a1124fb163))
* **kubernetes:** add local cluster setup guide ([2aa219e](https://github.com/Devops-is-fun/step-by-step/commit/2aa219e4de3227e47a66e7ee429516ec489843a1))
* **kubernetes:** add local cluster setup guide ([a9c28ca](https://github.com/Devops-is-fun/step-by-step/commit/a9c28ca763f5256c613c98cbd4362b4c17e06cd5))
* **kubernetes:** clarify ServiceMonitor installation order ([a252f91](https://github.com/Devops-is-fun/step-by-step/commit/a252f9135fd01fa2b843824a64f9239f773dfb33))
* **kubernetes:** test through the ingress, add autoscaling and resilience checks; keep the readme out of this PR ([39da411](https://github.com/Devops-is-fun/step-by-step/commit/39da411b3e009a706cb58beddcc395ad55fb6d1a))
* **kubernetes:** update installation guide for Flux GitOps ([bcad198](https://github.com/Devops-is-fun/step-by-step/commit/bcad198cb40373806c6409c4fcbc2ad76dd69103))
* **report:** add dora measurements and lessons learned ([9f3a5b5](https://github.com/Devops-is-fun/step-by-step/commit/9f3a5b5f6bb2216b3c8aac6a80ee895ff93c9d8a))
* **report:** add dora measurements and lessons learned ([9f3a5b5](https://github.com/Devops-is-fun/step-by-step/commit/9f3a5b5f6bb2216b3c8aac6a80ee895ff93c9d8a))
* **report:** add dora measurements and lessons learned ([70f9480](https://github.com/Devops-is-fun/step-by-step/commit/70f94809a57a6389e59ca13145959f2bae043ae4))
* rewrite the readme for the kubernetes / gitops delivery ([ff0f8e3](https://github.com/Devops-is-fun/step-by-step/commit/ff0f8e3ad3d7a75006bcf7afb1ddae8a352fb5ca))
* rewrite the readme for the kubernetes / gitops delivery ([4472abc](https://github.com/Devops-is-fun/step-by-step/commit/4472abc47f1a8ecd9a26862b428d51840eb952ee))

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
