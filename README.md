# 🐝 BIOBUZZ — פרויקט הכשרה מלא ל־FTC

פרויקט Android Studio מוכן לפתיחה ולהרצה, שמלווה את קורס התכנות של BIOBUZZ ל־FIRST Tech Challenge.
הפרויקט כולל את כל התשתית, 56 קובצי Java של מימושי השיעורים (ועוד 5 דוגמאות Autonomous של מדריך Pedro), דוגמאות הפעלה לכל שיעור ומפות שמקשרות בין השיעורים לקבצים.

![FTC SDK](https://img.shields.io/badge/FTC%20SDK-11.0.0-orange)
![Pedro Pathing](https://img.shields.io/badge/Pedro%20Pathing-2.1.2-blue)
![SolversLib](https://img.shields.io/badge/SolversLib-SNAPSHOT--7cdcc66-blueviolet)
![Java](https://img.shields.io/badge/Gradle%20JVM-Java%2021-red)
![Gradle](https://img.shields.io/badge/Gradle-8.9-02303A)

> 📘 **מתחילים כאן:** מדריך הפתיחה וההרצה המלא נמצא ב־[START-HERE-HE.txt](START-HERE-HE.txt).
>
> 🧭 **חדש: [מדריך Autonomous עם Pedro Pathing](PEDRO-AUTONOMOUS-GUIDE-HE.md)** — צירים וזוויות, פירוק האוטונומי של שיעור 10, חמש דוגמאות מלאות, Workflow ופתרון תקלות.

---

## ✨ מה בפרויקט

- **סגנון פקודות אחיד** — כל ההפעלות עוברות דרך `MMRobot.getInstance().<subsystem>.<command>()`, כמו ברובוט של BIOBUZZ.
- **פרויקט FTC מלא** — `FtcRobotController`, `TeamCode`, `MeepMeepTesting` ו־Gradle Wrapper.
- **מימושים מלאים להרצה** — מנוע יחיד, מאקאנום, מעלית עם PID, סרבו, קבוצות פקודות, חיישנים, Limelight ואוטונומי עם Pedro Pathing.
- **11 בדיקות יחידה** ללוגיקת שליטה ושילוב, שרצות ללא חומרה.
- **חומרי לימוד** — מפת שיעורים, מפת מחלקות וחוברת תרגול לבנייה עצמאית.

## 🚀 התחלה מהירה

1. מורידים את הפרויקט (`Code → Download ZIP` או `git clone`) ומחלצים לתיקייה.
2. ב־Android Studio: **Open** ← בוחרים את התיקייה שבה נמצא `settings.gradle`.
3. **Settings → Build, Execution, Deployment → Build Tools → Gradle** ← בוחרים **Gradle JDK = Java 21** (לא Java 25).
4. מאשרים Gradle Sync (נדרש אינטרנט בפעם הראשונה).
5. בוחרים `TeamCode` ← **Build → Make Project**.

בנייה והרצת בדיקות משורת הפקודה:

```bash
# macOS / Linux
sh ./gradlew :TeamCode:assembleDebug :TeamCode:testDebugUnitTest

# Windows
gradlew.bat :TeamCode:assembleDebug :TeamCode:testDebugUnitTest
```

קובצי הקוד נמצאים ב־`TeamCode/src/main/java/org/firstinspires/ftc/teamcode`.

## 📚 השיעורים

| # | נושא | הפעלה ב־Driver Station | שמות חומרה |
|---|------|------------------------|-------------|
| 1 | מבנה הפרויקט וקבוצות פקודות | Command Groups Lesson | `intake`, `transfer` |
| 2 | מנוע יחיד | Single Motor | `testMotor` |
| 3 | מאקאנום | Mecanum Lesson | `FL`, `FR`, `BL`, `BR` |
| 4 | מעלית ו־PID | Elevator PID Lesson | `lift`, `liftBottom`, `liftTop` |
| 5 | סרבו | Positional / Continuous Servo Lesson | `claw` / `roller` |
| 6 | סביבת עבודה ו־Git | Gradle Build | — |
| 7 | חיישנים ואנקודרים | Training Sensor Diagnostics | `intake`, `liftBottom` |
| 8 | פקודות ומכונות מצבים | Command Groups Lesson | `intake`, `transfer` |
| 9 | איתור תקלות ובדיקות | Gradle unit tests | — |
| 10 | אוטונומי וראייה | Training Short Line (Disabled) | `FL`, `FR`, `BL`, `BR` + Pinpoint |
| 11 | פרויקט הפרחים | תרגיל לוגיקה | לפי תכנון הקבוצה |
| 📷 | Limelight 3A | Limelight Aim Preview | `limelight` |

הפירוט המלא — קבצים לכל שיעור ותרגיל בנייה עצמאית — נמצא ב־[LESSON-MAP.csv](LESSON-MAP.csv).

## 🗂️ מבנה הקוד

```
teamcode/
├── Commands/        קבוצות פקודות (Intake, Shoot, TimedFeed)
├── Libraries/
│   ├── MMLib/       MMRobot, MMSystems, MMOpMode
│   ├── CuttlefishFTCBridge/   שכבת התקנים (מנועים, סרבו, אנקודרים)
│   └── pedroPathing/          Constants, Tuning, FollowPathCommand
├── OpMode/
│   ├── Auto/        BasicAuto
│   └── TeleOp/      הפעלות השיעורים
├── SubSystems/      מנוע, מאקאנום, מעלית, סרבו, Intake, Shooter, Limelight
└── training/        קוד ייעודי להכשרה (חיישנים, פקודות, Limelight, אוטונומי)
    └── guide/       חמש דוגמאות ה־Autonomous של מדריך Pedro
```

## 📄 קבצים חשובים

| קובץ | תוכן |
|------|------|
| [START-HERE-HE.txt](START-HERE-HE.txt) | מדריך פתיחה, בנייה והרצה ראשונה |
| [PEDRO-AUTONOMOUS-GUIDE-HE.md](PEDRO-AUTONOMOUS-GUIDE-HE.md) | מדריך Autonomous עם Pedro Pathing + 5 דוגמאות |
| [STUDENT-WORKBOOK-HE.txt](STUDENT-WORKBOOK-HE.txt) | סדר עבודה ויעדי תרגול |
| [LESSON-MAP.csv](LESSON-MAP.csv) | שיעור ← קבצים, הפעלה, חומרה ותרגיל |
| [CLASS-INVENTORY.csv](CLASS-INVENTORY.csv) | כל מחלקות ה־Java ותפקידן |
| [SOURCE-COVERAGE.csv](SOURCE-COVERAGE.csv) | התאמה בין הקוד במצגות לקוד בפרויקט |
| [PROJECT-MANIFEST.json](PROJECT-MANIFEST.json) | חתימות SHA-256 לבדיקת שלמות |
| [FTC-SDK-README.md](FTC-SDK-README.md) | התיעוד הרשמי של FTC SDK |

## ⚠️ חשוב לדעת

- **אין לשדרג ספריות** במהלך הלמידה — חומרי הקורס מתייחסים לגרסאות הקבועות.
- שמות ההתקנים חייבים להיות **זהים** בקוד וב־Robot Configuration, כולל אותיות גדולות.
- כיווני מנועים, גבהי מעלית, טווחי סרבו ומקדמי PID **תלויים ברובוט שלכם** וצריך לכייל אותם.
- הבדיקות הן בדיקות תוכנה בלבד; **לא בוצעה בדיקת חומרה פיזית**.
- `BasicAuto` הוא מסלול לימודי קצר (12 אינץ׳), מסומן `@Disabled` עד כיול Pedro — לא אוטונומי לתחרות.

## 📜 רישיון

מבוסס על [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) של FIRST — ראו [LICENSE](LICENSE).
