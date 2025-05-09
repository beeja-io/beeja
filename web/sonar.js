import 'dotenv/config';
import { spawnSync, execSync } from 'child_process';

execSync(
  `sonar-scanner
  -D"sonar.projectKey=${process.env.SONAR_PROJECT_KEY}"
  -D"sonar.sources=."
  -D"sonar.host.url=${process.env.SONAR_HOST_URL}"
  -D"sonar.token=${process.env.SONAR_TOKEN}"`,
  { stdio: 'inherit' }
);
