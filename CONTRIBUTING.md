# Contributing

  - We accept work via GitHub pull requests. 
  - We'll try to review your PR as soon as possible.
  - If your PR would fully resolve a ticket, please use "Closes #25 : resolved" (with the proper bug number of course).

#### Pull Request Process

  1. Ensure any install / build or IDE dependencies are removed before the end of the layer when doing a build.
  2. Be prepared to make a commit to the WIKI with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
  3. Increase the version numbers in any examples files and the README.md to the new version that this Pull Request would represent. The versioning scheme we use is [SemVer](http://semver.org/).
  4. The Pull Request can be merged in once you have the sign-off of one other developers, or if you do not have permission to do that, you may request the second reviewer to merge it for you.
  5. Please try to keep your code clean, and try to follow the formatting Prism uses already.

#### Our Branch Management Process

This may change if our needs to, but currently:

  - All non-urgent development towards the next release is done on `master`
  - We tag the repo for each release type and there may be branch created for the release to allow hot fixes to be easily applied.
  - Urgent bug fixes are applied to branches made from a release's tag. For example, 1.1 was tagged and released. Urgent bug fixes for 1.1.1 we're done on the branch `1.1`.
  - Work planned for each release are tracked by milestones matching the release numbers.
