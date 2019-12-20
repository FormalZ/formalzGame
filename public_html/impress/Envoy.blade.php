@servers(['web' => 'deployer@science-vs160.science.uu.nl']);

@setup
    $repository = 'git@git.science.uu.nl:SoftwareProjectG/SoftwareProject.git';
    $releases_dir = '/var/www/releases';
    $app_dir = '/var/www';
    $release = date('YmdHis');
    $new_release_dir = $releases_dir . '/' . $release;
@endsetup

@story('deploy')
    clone_repository
    run_composer
    run_npm
    update_frontend_ip
    build_phaser
    copy_keystore
    build_java
    update_symlinks
    restart_java
@endstory

@task('clone_repository')
    echo 'Cloning repository'
    [ -d {{ $releases_dir }} ] || mkdir {{ $releases_dir }}
    git clone --depth 1 {{ $repository }} {{ $new_release_dir }}
@endtask

@task('run_composer')
    echo "Starting deployment {{ $release }}, running composer"
    cd {{ $new_release_dir }}/public_html/impress
    composer install --no-dev --prefer-dist --no-scripts -q -o
@endtask

@task('run_npm')
    echo "Building the npm depedencies"
    cd {{ $new_release_dir }}/Front-end
    mv webpack.dist.config.js webpack.dist.config.js.backup
    npm install
    mv -f webpack.dist.config.js.backup webpack.dist.config.js
@endtask

@task('update_frontend_ip')
    echo "Updating the backend IP in phaser code"
    cd {{ $new_release_dir }}/Front-end/assets/json
    sed -i 's/"ipAddress":.*/"ipAddress": "science-vs160.science.uu.nl",/' settings.json
@endtask

@task('build_phaser')
    echo "Building the phaser front-end"
    cd {{ $new_release_dir }}/public_html/impress
    bash ./scripts/buildphaser.sh
@endtask

@task('copy_keystore')
    echo "Copying the keystore to the GameServer"
    cp -f /var/www/keystore.jks {{ $new_release_dir }}/GameServer/keystore.jks
@endtask

@task('build_java')
    echo "Building the java server"
    cd {{ $new_release_dir}}/GameServer
    ant BuildServer
@endtask

@task('update_symlinks')
    echo "Linking storage directory"
    rm -rf {{ $new_release_dir }}/public_html/impress/storage
    ln -nfs {{ $app_dir }}/storage {{ $new_release_dir }}/public_html/impress/storage

    echo "Linking .env file"
    ln -nfs {{ $app_dir }}/.env {{ $new_release_dir }}/public_html/impress/.env

    echo "Linking current release"
    rm -rf {{ $app_dir }}/current
    ln -nfs {{ $new_release_dir }} {{ $app_dir }}/current
@endtask

@task('restart_java')
    echo "Restarting the java server"
    cd {{ $new_release_dir}}/GameServer
    pkill java
    sleep 10
    nohup java -jar server.jar </dev/null >server.log 2>&1 & disown
@endtask

