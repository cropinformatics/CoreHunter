###########################
# load Core Hunter into R #
###########################

# function to run Core Hunter
corehunter.run <- function(options, mem="512m"){
    # set path to core hunter CLI
    cli = "corehunter.jar"
    if(!file.exists(cli)){
        cli = "corehunter-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/corehunter-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/corehunter.jar"
    }
    if(!file.exists(cli)){
        stop("Core Hunter CLI jar file not found.")
    }
    # run CLI
    mempar = paste("-Xmx", mem, sep="");
    system(paste("java", mempar, "-jar", cli, options))
}

# function to run Core Analyser
coreanalyser.run <- function(options, mem="512m"){
    # set path to core analyser CLI
    cli = "coreanalyser.jar"
    if(!file.exists(cli)){
        cli = "coreanalyser-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/coreanalyser-cli.jar"
    }
    if(!file.exists(cli)){
        cli = "bin/coreanalyser.jar"
    }
    if(!file.exists(cli)){
        stop("Core Analyser CLI jar file not found.")
    }
    # run CLI
    mempar = paste("-Xmx", mem, sep="");
    system(paste("java", mempar, "-jar", cli, options))
}