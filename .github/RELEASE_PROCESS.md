# Release Process

This document describes the CI/CD and release process for Finny.

## Workflow Overview

```
Feature Branch → Pull Request → Tests Run → Merge to Main → Automatic Release
```

## Branch Strategy

- **Main/Master**: Production-ready code. All merges trigger a release.
- **Feature Branches**: `feature/*`, `fix/*`, etc. - Created from main for new work.

## Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes and Commit**
   ```bash
   git add .
   git commit -m "Add your feature"
   git push origin feature/your-feature-name
   ```

3. **Create Pull Request**
   - Go to GitHub and create a PR from your feature branch to `main`
   - GitHub Actions will automatically run tests
   - PR cannot be merged until tests pass

4. **Review and Merge**
   - Wait for CI to pass (green checkmark)
   - Get code review if required
   - Merge the PR

5. **Automatic Release**
   - Upon merge to `main`, the release workflow automatically:
     - Runs tests again (safety check)
     - Builds the application JAR
     - Creates a new GitHub release with timestamp version
     - Attaches the JAR file to the release
     - Generates release notes from commits

## Setting Up Branch Protection

To enforce that tests must pass before merging, configure branch protection in GitHub:

### Steps:

1. **Go to Repository Settings**
   - Navigate to your repository on GitHub
   - Click **Settings** (top menu)

2. **Configure Branch Protection**
   - In the left sidebar, click **Branches**
   - Click **Add branch protection rule** (or edit existing rule)

3. **Configure Protection Rule**
   - **Branch name pattern**: `main` (or `master`)

   - **Protect matching branches** - Enable these options:
     - ✅ **Require a pull request before merging**
       - Optional: Require approvals (set to 0 if you work solo)

     - ✅ **Require status checks to pass before merging**
       - ✅ **Require branches to be up to date before merging**
       - In the search box, find and select: **Run Tests** (this is the job name from ci.yml)

     - ✅ **Do not allow bypassing the above settings** (recommended)

4. **Save Changes**
   - Click **Create** or **Save changes**

## CI Workflow (ci.yml)

**Triggers:**
- On pull request to main/master
- On push to main/master

**Actions:**
- Checks out code
- Sets up JDK 21
- Runs `./gradlew :composeApp:jvmTest`
- Publishes test results
- Uploads test reports as artifacts

**Result:**
- PR shows green checkmark if tests pass
- PR shows red X if tests fail
- Merge button is disabled if tests fail (with branch protection)

## Release Workflow (release.yml)

**Triggers:**
- On push to main/master (after PR merge)

**Actions:**
- Runs full test suite
- Builds application JAR
- Creates Git tag with version (format: `vYYYY.MM.DD.HHMMSS`)
- Creates GitHub release
- Attaches JAR file
- Auto-generates release notes from commits

**Version Format:**
- Automatic timestamp-based versioning: `v2026.02.25.143000`
- Can be customized to read from `build.gradle.kts` if needed

## Viewing Releases

1. Go to your GitHub repository
2. Click **Releases** (right sidebar)
3. See all releases with:
   - Version tag
   - Release notes (auto-generated from commits)
   - Attached JAR file for download

## Local Testing Before Push

Always test locally before pushing:

```bash
# Run tests
./gradlew :composeApp:jvmTest

# Build application
./gradlew :composeApp:jvmJar

# Run application
./gradlew :composeApp:run
```

## Troubleshooting

### Tests Pass Locally But Fail in CI

- Check that you've committed all necessary files
- Ensure dependencies are defined in `build.gradle.kts`
- Review CI logs in GitHub Actions tab

### Release Not Created

- Check GitHub Actions logs in the **Actions** tab
- Ensure repository has **Contents: Write** permission enabled
- Verify the workflow file is on the main/master branch

### Cannot Merge PR

- Ensure tests pass (green checkmark)
- Check that branch protection rules are correctly configured
- Make sure your branch is up to date with main

## Customizing Versioning

To use semantic versioning instead of timestamp-based:

1. Edit `.github/workflows/release.yml`
2. Replace the "Get version" step with:
   ```yaml
   - name: Get version
     id: get_version
     run: |
       # Read version from build.gradle.kts
       VERSION=$(grep "packageVersion" composeApp/build.gradle.kts | sed 's/.*"\(.*\)".*/\1/')
       echo "version=v${VERSION}" >> $GITHUB_OUTPUT
   ```

3. Update `packageVersion` in `composeApp/build.gradle.kts` before merging

## Benefits of This Setup

✅ **No manual release work** - Everything is automated
✅ **Tests always run** - Cannot merge broken code
✅ **Complete audit trail** - All releases tied to commits
✅ **Rollback capability** - Previous releases available for download
✅ **Low overhead** - Minimal configuration, runs automatically
✅ **Professional workflow** - Industry-standard practices
