podTemplate(
    cloud: 'dev-cluster',
    namespace: 'kube-system',
    volumes: [
        hostPathVolume(
            hostPath: '/var/run',
            mountPath: '/home/jenkins/dind/'
        )
    ],
    name: 'go-cloud',
    label: 'go-cloud',
    idleMinutes: 60,
    containers: [
        // jnlp with kubectl
        containerTemplate(
            name: 'jnlp',
            alwaysPullImage: true,
            image: 'cargo.caicloud.io/circle/jnlp:2.62',
            command: '',
            args: '${computer.jnlpmac} ${computer.name}',
        ),
        containerTemplate(
            name: 'golang',
            image: 'cargo.caicloud.io/caicloud/golang:1.6',
            ttyEnabled: true,
            command: 'cat',
            args: '',
            envVars: [
                containerEnvVar(key: 'GOPATH', value: '/go'),
                containerEnvVar(key: 'WORKDIR', value: '/go/src/github.com/ustcliao/go-cloud')
            ]
        ),
        containerTemplate(
            name: 'docker-client',
            image: 'cargo.caicloud.io/caicloud/docker:1.8',
            ttyEnabled: true,
            command: '',
            args: '',
            envVars: [
                containerEnvVar(key: 'DOCKER_HOST', value: 'unix:///home/jenkins/dind/docker.sock')
            ]
        )
    ]
) {
    node('go-cloud') {
        stage('checkout') {
            checkout scm
        }
        
        // load config file
        load 'jenkins/config.groovy'

        container('golang') {
            stage('Compile') {
                sh('''
                    set -e 
                    mkdir -p $(dirname ${WORKDIR}) 

                    # if you do not remove target dir manually
                    # ln will not work according to what you want
                    # ln link /home/jenkins/workspace/xxxx to /go/src/github.com/caicloud/taas-serving-image at first time
                    # ln will link /home/jenkins/workspace/xxxx to /go/src/github.com/caicloud/taas-serving-image/xxx at second time
                    # so remove the target workdir before you link
                    rm -rf ${WORKDIR}
                    ln -sfv $(pwd) ${WORKDIR}
                    cd ${WORKDIR}

                    make clean
                    make compile-dev
                ''')
            }
        }
        container('docker-client') {
            stage('Image Build') {
                sh "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} -f ${env.DOCKERFILE} ."
            }
            

            stage ("Image Publish") {
                docker.withRegistry("https://${env.PRIVATE_CARGO_REGISTRY}", "cargo-private-admin") {
                    docker.image("${env.IMAGE_NAME}:${env.IMAGE_TAG}").push()
                }
                if ("${env.PUBLISH_TO_PUBLIC}" == "true") {
                    docker.withRegistry("https://${env.PUBLIC_CARGO_REGISTRY}", "cargo-public-admin") {
                        docker.image("${env.IMAGE_NAME}:${env.IMAGE_TAG}").push()
                    }
                }
            }
        }
    }
}

