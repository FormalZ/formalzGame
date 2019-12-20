# Set the base image for subsequent instructions
FROM milk/php-xdebug:7.1

# Add support for newer java packages (JDK 8) to our package manager:
RUN echo "deb http://http.debian.net/debian jessie-backports main" >> /etc/apt/sources.list

# Update packages
RUN apt-get update

# Install PHP and composer dependencies
RUN apt-get install -qq git curl wget libmcrypt-dev libjpeg-dev libpng-dev libfreetype6-dev libbz2-dev

# Install Java packages
RUN apt-get install -qq -t jessie-backports openjdk-8-jdk && \
    apt-get install -qq ant && \
    apt-get clean;

# Install Phaser build packages
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash -
RUN apt-get install -qq nodejs
RUN npm install --global npm@5.6.0
#install chromium for testing:
RUN apt-get install -y gconf-service libasound2 libatk1.0-0 libcairo2 libcups2 libfontconfig1 libgdk-pixbuf2.0-0 libgtk-3-0 libnspr4 libpango-1.0-0 libxss1 fonts-liberation libappindicator3-1 libnss3 lsb-release xdg-utils
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb
RUN apt-get -fy install

# Clear out the local repository of retrieved package files
RUN apt-get clean

# Install needed extensions in PHP
RUN docker-php-ext-install mcrypt pdo_mysql zip

# Install Composer
RUN curl --silent --show-error https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer

# Install Laravel Envoy
RUN composer global require "laravel/envoy=~1.0"
