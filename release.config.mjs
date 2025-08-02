import { execSync } from "child_process";

const MAINTENANCE_BRANCH_PREFIX = "origin/mc/";
const TAG_REGEX = /^v?(\d+\.\d+\.\d+)(?:\+.+)?$/;

/**
 * Gets the latest version based on the tags from a specific git branch.
 * @param {string} branchName The name of the branch (e.g., 'origin/mc/1.21.7').
 * @returns {string} The version string (e.g., '1.6.0').
 */
function getVersionFromBranch(branchName) {
  const latestTag = execSync(`git describe --tags --abbrev=0 ${branchName}`, {
    encoding: "utf-8",
  }).trim();

  const match = latestTag.match(TAG_REGEX);
  if (!match) {
    throw new Error(
      `Version "${latestTag}" on branch ${branchName} does not match expected format.`
    );
  }

  return match[1];
}

/**
 * Creates a version range string from a given version.
 * @param {string} version - The version string (e.g., '1.6.0').
 * @returns {string} The version range string (e.g., '1.6.x').
 */
function makeVersionRange(version) {
  const [major, minor] = version.split(".");
  return `${major}.${minor}.x`;
}

const config = {
  plugins: [
    "@semantic-release/commit-analyzer",
    "@semantic-release/release-notes-generator",
    [
      "@semantic-release/exec",
      {
        prepareCmd: './release/prepare.sh "${nextRelease.version}"',
        publishCmd: "./release/publish.sh \"$(cat << 'EOF'\n${nextRelease.notes}\nEOF\n)\"",
      },
    ],
    [
      "@semantic-release/github",
      {
        assets: ["build/libs/*.jar"],
        releaseNameTemplate: "${nextRelease.version} for Minecraft <%= process.env.MINECRAFT_VERSION %>",
      },
    ],
  ],
  branches: [{ name: "main" }],
};

const branches = execSync("git branch -r", { encoding: "utf-8" }).split("\n");

for (const branch of branches) {
  const branchName = branch.trim();
  if (!branchName.startsWith(MAINTENANCE_BRANCH_PREFIX)) {
    continue;
  }

  const version = getVersionFromBranch(branchName);
  const shortBranchName = branchName.replace("origin/", "");

  config.branches.push({
    name: shortBranchName,
    range: makeVersionRange(version),
    prerelease: false,
  });
}

// console.log(
//   "Generated semantic-release config:",
//   JSON.stringify(config, null, 2)
// );

export default config;
