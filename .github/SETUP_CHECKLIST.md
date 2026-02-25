# CI/CD Setup Checklist

Follow these steps to complete the setup:

## ✅ Step 1: Commit and Push Workflows

The GitHub Actions workflow files have been created:
- `.github/workflows/ci.yml` - Runs tests on PRs
- `.github/workflows/release.yml` - Creates releases on merge

**Action Required:**
```bash
git add .github/
git commit -m "Add CI/CD workflows"
git push origin main
```

## ✅ Step 2: Configure Branch Protection

1. Go to: https://github.com/YOUR_USERNAME/finny/settings/branches
2. Click **Add branch protection rule**
3. Set **Branch name pattern**: `main`
4. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
   - Search and select: **Run Tests**
5. Click **Create**

## ✅ Step 3: Test the Workflow

Create a test feature branch:

```bash
# Create feature branch
git checkout -b feature/test-ci

# Make a small change
echo "# CI/CD Testing" >> README.md

# Commit and push
git add README.md
git commit -m "Test CI workflow"
git push origin feature/test-ci
```

Then:
1. Go to GitHub and create a Pull Request
2. Watch the CI workflow run
3. Verify tests pass (green checkmark)
4. Try merging - it should work
5. Watch the Release workflow create a new release

## ✅ Step 4: Verify Release

After merging the test PR:
1. Go to: https://github.com/YOUR_USERNAME/finny/releases
2. You should see a new release with:
   - Timestamp-based version (e.g., `v2026.02.25.143000`)
   - Auto-generated release notes
   - JAR file attached

## ✅ Step 5: Clean Up Test Branch

```bash
# Switch back to main
git checkout main
git pull origin main

# Delete test branch
git branch -d feature/test-ci
git push origin --delete feature/test-ci
```

## ✅ Optional: Customize Settings

### Change Version Format

Edit `.github/workflows/release.yml` line 37-40 to use semantic versioning instead of timestamps.

### Require Code Review

In branch protection settings:
- Set "Require approvals" to 1 or more

### Add Build Artifacts

To include additional files in releases, edit `.github/workflows/release.yml` under `files:`.

## 📚 Documentation

- Full process: `.github/RELEASE_PROCESS.md`
- For help: Create an issue in the repository

## 🎉 You're Done!

Your repository now has:
- ✅ Automated testing on PRs
- ✅ Protection against merging broken code
- ✅ Automatic releases on every merge to main
- ✅ Complete CI/CD pipeline with minimal overhead
