# Development

This document outlines how to work with the project.

## Commit format

Our releases are automated and depend on commit messages to follow the
Conventional Commits standard. You can read up on it here:
https://www.conventionalcommits.org.

## Supported Minecraft versions

We only implement new features for the latest supported version of Minecraft.
Our releases only ever support one version of Minecraft at a time. Even though
it's technically possible to sometimes support multiple versions of Minecraft at
the same time, for example when there've been no API changes, this adds a lot of
complexity.

We do however fix bugs in older versions of Minecraft. There is no set rule for
how many versions we support and it's up to the core maintainers' discression to
decide what bugs are fixed.

## Branches and automatic releases

Releases in this repository are automated. Pushing to any of the following
release branches triggers a build and release to Modrinth:
- `main`
- `mc/*` eg. `mc/1.21.3`. These branches are called "maintenance branches".

You cannot push directly to a release branch, but must instead raise a pull
request. The pull request can either originate from a fork or from the
repository itself. While external contributors are required to use forks,
contributors with write access are encouraged to create branches in the
repository for easier collaboration.

When creating a pull request towards a release branch our workflows will leave a
comment on the PR indicating if the PR will trigger a release. There are also
checks run to ensure your PR won't put the target branch in a broken state, for
example by upgrading the Minecraft version of a maintenance branch.

> [!NOTE]  
> We only implement new features on the main release branch. Maintenance
> branches only accept bug fixes, and pushing a feature or breaking change to
> one will cause the release process to fail and require manually removing or
> rewording the broken commit and force pushing.

When upgrading the mod to a newer version of Minecraft a maintenance branch must
be created for the previous version. A PR that bumps the Minecraft version will
be blocked from merging unless a maintenance branch has been created for the
current version.

### Examples

<details>
  <summary>✨ Implementing a feature on the main branch</summary>

  ```sh
  # Checkout the main branch
  git checkout main
  git pull

  # Create a feature branch
  git checkout -b my-cool-feature

  # Implement your feature and push your changes
  git add -A
  git commit -m "feat: my cool feature"
  git push -u origin my-cool-feature

  # Finally, create a pull request towards the main branch.
  # When merged, a new version will be released automatically.
  ```
</details>

<details>
  <summary>🐛 Fixing a bug for an older version of Minecraft</summary>

  ```sh
  # Checkout the maintenance branch
  git checkout mc/1.21.3
  git pull

  # Create a branch for your fix
  git checkout -b my-fix

  # Fix the bug and push your changes
  git add -A
  git commit -m "fix: did something"
  git push -u origin my-fix

  # Finally, create a pull request towards the maintenance branch.
  # When merged, a new version will be released automatically.
  ```
</details>

<details>
  <summary>⬆️ Upgrading to a newer version of Minecraft</summary>

  ```sh
  # Checkout the main branch
  git checkout main
  git pull

  # Create the maintenance branch. The name should match the
  # version of Minecraft currently supported on the main branch.
  git checkout -b mc/1.21.4
  git push -u origin mc/1.21.4

  # Switch back to the main branch
  git checkout main

  # Create a feature branch
  git checkout -b upgrade-to-mc-1.21.5

  # Upgrade the Minecraft version and push your changes
  git add -A
  git commit -m "feat: Upgrade to Minecraft 1.21.5"
  git push -u origin upgrade-to-mc-1.21.5

  # Finally, create a pull request towards the main branch.
  # When merged, a new version will be released automatically.
  ```
</details>

<details>
  <summary>📦 Grouping multiple changes into one release</summary>

  Sometimes you may have multiple pull request that must be released together.
  This is easily done by creating an empty branch and targeting your other pull
  requests towards this branch. When you've merged all pull requests to the new
  branch, create a pull request for that branch as if it were any other feature
  branch.

  ```sh
  # Checkout the main branch
  git checkout main
  git pull

  # Create and push an empty feature branch
  git checkout -b my-grouped-changes
  git push -u origin my-grouped-changes
  ```
</details>
