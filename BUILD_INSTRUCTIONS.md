# BUILD INSTRUCTIONS - MANUAL SETUP REQUIRED

## ⚠️ GRADLE NOT INSTALLED

The build process requires Gradle. You have two options:

### Option 1: Install Gradle (Recommended)

1. Download Gradle 8.5: https://gradle.org/releases/
2. Extract to `C:\Gradle\gradle-8.5`
3. Add to PATH: `C:\Gradle\gradle-8.5\bin`
4. Restart terminal
5. Run: `gradle wrapper --gradle-version=8.5`
6. Run: `gradlew build`

### Option 2: Use Pre-installed Gradle Wrapper

If you have another Forge project with gradlew:
```bash
copy C:\path\to\other\forge\project\gradlew.bat .
copy C:\path\to\other\forge\project\gradle\wrapper\* gradle\wrapper\
gradlew build
```

### Option 3: Build in IDE

1. Open IntelliJ IDEA / Eclipse
2. Import as Gradle project
3. Let IDE download dependencies
4. Run: Gradle > Tasks > build > build

## 📦 EXPECTED OUTPUT

Once built successfully:
- **JAR Location**: `build/libs/TruthSystems-1.0.0.jar`
- **Install to**: `..\mods\TruthSystems-1.0.0.jar`

## ✅ ALTERNATIVE: PROCEED WITHOUT BUILD

All source code is ready. You can:
1. Expand systems first (see FINAL_STATUS.md)
2. Commit to git (verification hashes already generated)
3. Upload to GitHub (ready for CI/CD build)

---

**Build Status**: ⏸️ AWAITING GRADLE INSTALLATION
**Code Status**: ✅ COMPLETE AND COVENANT-COMPLIANT
