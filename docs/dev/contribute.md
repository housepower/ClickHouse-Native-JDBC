Contributing Guide
==================
We welcome anyone that wants to help out in any way, whether that includes reporting problems, helping with documentations, or contributing code changes to fix bugs, add tests, or implement new features. This document outlines the basic steps required to work with and contribute to the codebase.

### Create issue

You can report problems or request features by creating [GitHub Issues](https://github.com/housepower/ClickHouse-Native-JDBC/issues).

### Install the tools

The following software is required to work with the codebase and build it locally:

* Git
* JDK 8/11
* Maven
* Docker and docker-compose

You can verify the tools are installed and running:

    $ git --version
    $ javac -version
    $ mvn -version
    $ docker --version
    $ docker-compose --version

### GitHub account

If you don't already have a GitHub account you'll need to [join](https://github.com/join).

### Fork the repository

Go to the [ClickHouse-Native-JDBC repository](https://github.com/housepower/ClickHouse-Native-JDBC) and press the "Fork" button near the upper right corner of the page. When finished, you will have your own "fork" at `https://github.com/<your-username>/ClickHouse-Native-JDBC`, and this is the repository to which you will upload your proposed changes and create pull requests. For details, see the [GitHub documentation](https://help.github.com/articles/fork-a-repo/).

### Clone your fork

At a terminal, go to the directory in which you want to place a local clone of the ClickHouse-Native-JDBC repository, and run the following commands to use HTTPS authentication:

    $ git clone https://github.com/<your-username>/ClickHouse-Native-JDBC.git

If you prefer to use SSH and have [uploaded your public key to your GitHub account](https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/), you can instead use SSH:

    $ git clone git@github.com:<your-username>/ClickHouse-Native-JDBC.git

This will create a `ClickHouse-Native-JDBC` directory, so change into that directory:

    $ cd ClickHouse-Native-JDBC

This repository knows about your fork, but it doesn't yet know about the official or ["upstream" housepower repository](https://github.com/housepower/ClickHouse-Native-JDBC). Run the following commands:

    $ git remote add upstream https://github.com/housepower/ClickHouse-Native-JDBC.git
    $ git fetch upstream
    $ git branch --set-upstream-to=upstream/master master

Now, when you check the status using Git, it will compare your local repository to the *upstream* repository.

### Get the latest upstream code

You will frequently need to get all the of the changes that are made to the upstream repository, and you can do this with these commands:

    $ git fetch upstream
    $ git pull upstream master

The first command fetches all changes on all branches, while the second actually updates your local `master` branch with the latest commits from the `upstream` repository.

### Building locally

To build the source code locally, checkout and update the `master` branch:

    $ git checkout master
    $ git pull upstream master

Then use Maven to compile everything, run all unit and integration tests, build all artifacts, and install all JAR, ZIP, and TAR files into your local Maven repository:

    $ mvn clean install

If you want to skip the integration tests (e.g., if you don't have Docker installed) or the unit tests, you can add `-DskipITs` and/or `-DskipTests` to that command:

    $ mvn clean install -DskipITs -DskipTests

### Running and debugging tests

A number of the modules use Docker during their integration tests to run a database. During development it's often desirable to start the Docker container and leave it running so that you can compile/run/debug tests repeatedly from your IDE. To do this, simply go into project directory and run the following command:

    $ docker-compose up -d

This will first pull Docker image for the database container, and then will start it. You can then run any integration tests from your IDE.

When your testing is complete, you can stop the Docker container by running:

    $ docker-compose down

### Making changes

Everything the community does with the codebase -- fixing bugs, adding features, making improvements, adding tests, etc. -- should be described by [GitHub Issue](https://github.com/housepower/ClickHouse-Native-JDBC/issues). If no such issue exists for what you want to do, please create an issue with a meaningful and easy-to-understand description.
If you are going to work on a specific issue and it's your first contribution, please add a short comment to the issue, so other people know you're working on it.
If you are contributing repeatedly, you will be invited to be a Core Team member so you can assign issues to yourself.

Before you make any changes, be sure to switch to the `master` branch and pull the latest commits on the `master` branch from the upstream repository. Also, it's probably good to run a build and verify all tests pass *before* you make any changes.

    $ git checkout master
    $ git pull upstream master
    $ mvn clean install

Once everything builds, create a *topic branch* named appropriately (we recommend using the issue number, such as `gh-1234`):

    $ git checkout -b gh-1234

This branch exists locally and it is there you should make all of your proposed changes for the issue. As you'll soon see, it will ultimately correspond to a single pull request that the Core Team member members will review and merge (or reject) as a whole. (Some issues are big enough that you may want to make several separate but incremental sets of changes. In that case, you can create subsequent topic branches for the same issue by appending a short suffix to the branch name.)

Your changes should include changes to existing tests or additional unit and/or integration tests that verify your changes work. We recommend frequently running related unit tests (in your IDE or using Maven) to make sure your changes didn't break anything else, and that you also periodically run a complete build using Maven to make sure that everything still works:

    $ mvn clean verify

Feel free to commit your changes locally as often as you'd like, though we generally prefer that each commit represent a complete and atomic change to the code. Often, this means that most issues will be addressed with a single commit in a single pull-request, but other more complex issues might be better served with a few commits that each make separate but atomic changes. (Some developers prefer to commit frequently and to ammend their first commit with additional changes. Other developers like to make multiple commits and to then squash them. How you do this is up to you. However, *never* change, squash, or ammend a commit that appears in the history of the upstream repository.) When in doubt, use a few separate atomic commits; if the reviewers think they should be squashed, they'll let you know when they review your pull request.

Committing is as simple as:

    $ git commit .

which should then pop up an editor of your choice in which you should place a good commit message. _*We do expect that all commit messages begin with a line starting with the GitHub issue and ending with a short phrase that summarizes what changed in the commit.*_ For example:

    gh-1234 Expanded the integration test and correct a unit test.

If that phrase is not sufficient to explain your changes, then the first line should be followed by a blank line and one or more paragraphs with additional details. For example:

```
gh-1235 Added support for ingesting data from Spark 3.0.

As Apache Spark 3.0 has been released, it's next major version since 2.0,
we need to add integration test to make sure the code work properly with
the new Spark version.
```

### Code Formatting

This project utilizes a set of code style rules that are automatically applied by the build process.

With the command `mvn validate` the code style rules can be applied automatically.

In the event that a pull request is submitted with code style violations, continuous integration will fail the pull request build.  

To fix pull requests with code style violations, simply run the project's build locally and allow the automatic formatting happen.  Once the build completes, you will have some local repository files modified to fix the coding style which can be amended on your pull request.  Once the pull request is synchronized with the formatting changes, CI will rerun the build.

To run the build, navigate to the project's root directory and run:

    $ mvn clean verify

It might be useful to simply run a _validate_ check against the code instead of automatically applying code style changes.  If you want to simply run validation, navigate to the project's root directory and run:

    $ mvn clean install -Dformat.formatter.goal=validate -Dformat.imports.goal=check     

Please note that when running _validate_ checks, the build will stop as soon as it encounters its first violation.  This means it is necessary to run the build multiple times until no violations are detected.

### Rebasing

If it's been more than a day or so since you created your topic branch, we recommend *rebasing* your topic branch on the latest `master` branch. This requires switching to the `master` branch, pulling the latest changes, switching back to your topic branch, and rebasing:

    $ git checkout master
    $ git pull upstream master
    $ git checkout gh-1234
    $ git rebase master

If your changes are compatible with the latest changes on `master`, this will complete and there's nothing else to do. However, if your changes affect the same files/lines as other changes have since been merged into the `master` branch, then your changes conflict with the other recent changes on `master`, and you will have to resolve them. The git output will actually tell you need to do (e.g., fix a particular file, stage the file, and then run `git rebase --continue`), but if you have questions consult Git or GitHub documentation or spend some time reading about Git rebase conflicts on the Internet.

### Documentation

When adding new features or configuration options, they must be documented accordingly in the Documents.
The same applies when changing existing behaviors, e.g. type mappings, removing options etc.

The documentation is written using Markdown and can be found in the ClickHouse-Native-JDBC [source code repository](https://github.com/housepower/ClickHouse-Native-JDBC/docs).
Any documentation update should be part of the pull request you submit for the code change.

The documentation will be published on the website when PR merged into master branch.

### Creating a pull request

Once you're finished making your changes, your topic branch should have your commit(s) and you should have verified that your branch builds successfully. At this point, you can shared your proposed changes and create a pull request. To do this, first push your topic branch (and its commits) to your fork repository (called `origin`) on GitHub:

    $ git push origin gh-1234

Then, in a browser go to https://github.com/housepower/ClickHouse-Native-JDBC, and you should see a small section near the top of the page with a button labeled "Create pull request". GitHub recognized that you pushed a new topic branch to your fork of the upstream repository, and it knows you probably want to create a pull request with those changes. Click on the button, and GitHub will present you with a short form that you should fill out with information about your pull request. The title should start with the GitHub issue and ending with a short phrase that summarizes the changes included in the pull request. (If the pull request contains a single commit, GitHub will automatically prepopulate the title and description fields from the commit message.)

At this point, you can switch to another issue and another topic branch. The maintainers will be notified of your new pull request, and will review it in short order. They may ask questions or make remarks using line notes or comments on the pull request. (By default, GitHub will send you an email notification of such changes, although you can control this via your GitHub preferences.)

If the reviewers ask you to make additional changes, simply switch to your topic branch for that pull request:

    $ git checkout gh-1234

and then make the changes on that branch and either add a new commit or ammend your previous commits. When you've addressed the reviewers' concerns, push your changes to your `origin` repository:

    $ git push origin gh-1234

GitHub will automatically update the pull request with your latest changes, but we ask that you go to the pull request and add a comment summarizing what you did. This process may continue until the reviewers are satisfied.

By the way, please don't take offense if the reviewers ask you to make additional changes, even if you think those changes are minor. The reviewers have a broach understanding of the codebase, and their job is to ensure the code remains as uniform as possible, is of sufficient quality, and is thoroughly tested. When they believe your pull request has those attributes, they will merge your pull request into the official upstream repository.

Once your pull request has been merged, feel free to delete your topic branch both in your local repository:

    $ git branch -d gh-1234

and in your fork:

    $ git push origin :gh-1234

(This last command is a bit strange, but it basically is pushing an empty branch (the space before the `:` character) to the named branch. Pushing an empty branch is the same thing as removing it.)

### Continuous Integration

The project currently builds its jobs in two environments:

- GitHub Actions for pull requests: https://github.com/housepower/ClickHouse-Native-JDBC/actions

### Summary

Here's a quick check list for a good pull request (PR):

* A GitHub issue associated with your PR
* One commit per PR
* One feature/change per PR
* No changes to code not directly related to your change (e.g. no formatting changes or refactoring to existing code, if you want to refactor/improve existing code that's a separate discussion and separate GitHub issue)
* New/changed features have been documented
* A full build completes successfully
* Do a rebase on upstream `master`