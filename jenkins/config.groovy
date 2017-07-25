env.PRIVATE_CARGO_REGISTRY = "cargo.caicloudprivatetest.com"
env.PUBLIC_CARGO_REGISTRY = "cargo.caicloud.io"
env.IMAGE_NAME = "caicloud/go-cloud-liaoyin"

VERSION_PREFIX = "v2.0."
PATCH_BASE = 1
env.PUBLISH_TO_PUBLIC = false

if ("${env.BRANCH_NAME}".toLowerCase().contains("pr-")) {
    env.IMAGE_TAG = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}".toLowerCase()

} else if ("${env.BRANCH_NAME}" == "master") {
    VERSION_PATCH = env.BUILD_NUMBER
    if (env.BUILD_NUMBER.toInteger() > PATCH_BASE) {
        VERSION_PATCH = env.BUILD_NUMBER.toInteger() - PATCH_BASE
    }
    env.IMAGE_TAG = "${VERSION_PREFIX}${VERSION_PATCH}"
} else {
    env.IMAGE_TAG = "${env.BRANCH_NAME}-rc${env.BUILD_NUMBER}"
    env.PUBLISH_TO_PUBLIC = true
}

env.DOCKERFILE = "./Dockerfile"

