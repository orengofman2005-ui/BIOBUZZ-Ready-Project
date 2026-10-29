# 🐝 מדריך Autonomous עם Pedro Pathing — BIOBUZZ

**למי המדריך מיועד:** תלמידי כיתות ח'–י"ב בקבוצת FTC, שכבר מכירים קצת Java ואת מבנה הפרויקט מהשיעורים הקודמים.
**על מה הוא מבוסס:** קובץ האוטונומי של שיעור 10 בפרויקט — [`OpMode/Auto/BasicAuto.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/OpMode/Auto/BasicAuto.java) — והקבצים שהוא משתמש בהם.
**גרסאות:** Pedro Pathing 2.1.2 · SolversLib SNAPSHOT-7cdcc66 · FTC SDK 11.0.0 (כמו בכל הפרויקט).

> ## ⛔ חוק ברזל: עובדים רק בתוך `TeamCode`
>
> **אסור לשנות, לערוך, למחוק או להוסיף שום דבר בתיקיות שמחוץ ל־`TeamCode`** — לא ב־`FtcRobotController`, לא ב־`MeepMeepTesting`, לא ב־`gradle`, לא בקובצי ה־`build.gradle` של השורש ולא בשום קובץ הגדרות אחר.
>
> כל הקוד של הקבוצה — OpModes, תתי־מערכות, מסלולים וכיול Pedro — נכתב **אך ורק** בתוך:
> `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`
>
> בפרק 1 מוסבר למה. אם נראה לכם שחייבים לשנות משהו בחוץ — עוצרים ושואלים מנטור.

---

## תוכן העניינים

1. [מבוא וסדר בסביבת העבודה](#1-מבוא-וסדר-בסביבת-העבודה)
2. [מושגי יסוד: צירים, קואורדינטות וזוויות](#2-מושגי-יסוד-צירים-קואורדינטות-וזוויות)
3. [פירוק הקובץ: איך BasicAuto עובד](#3-פירוק-הקובץ-איך-basicauto-עובד)
4. [חמש דוגמאות מלאות מקצה לקצה](#4-חמש-דוגמאות-מלאות-מקצה-לקצה)
5. [איך להריץ ולבדוק (Workflow)](#5-איך-להריץ-ולבדוק-workflow)
6. [תקלות נפוצות ואיך לפתור אותן](#6-תקלות-נפוצות-ואיך-לפתור-אותן)
7. [צ'קליסט לפני כל ריצה](#7-צקליסט-לפני-כל-ריצה)

---

## 1. מבוא וסדר בסביבת העבודה

### 1.1 מה אנחנו הולכים לבנות

בתחילת כל משחק FTC יש **תקופת Autonomous** של 30 שניות. בזמן הזה אסור לגעת בשלט — הרובוט חייב לנסוע, לאסוף ולנקד לבד, רק לפי הקוד שכתבנו.

כדי שהרובוט יגיע בדיוק לאן שצריך, אנחנו משתמשים בספרייה **Pedro Pathing** (בקיצור: Pedro). אנחנו אומרים לה "סעי מכאן לשם, בדרך הזו, ותסתכלי לכיוון הזה", והיא דואגת לכל החישובים: איפה הרובוט נמצא עכשיו, כמה הוא סטה מהמסלול, ואיזה כוח לתת לכל אחד מארבעת הגלגלים כדי לתקן.

במדריך נעבור שלב אחרי שלב:

- נבין איך Pedro "רואה" את המגרש (צירים, קואורדינטות וזוויות).
- נפרק את קובץ האוטונומי של שיעור 10 ונבין כל שורה בו.
- נכתוב **חמישה** אוטונומיים מלאים, מהפשוט ביותר (נסיעה ישרה) ועד אוטונומי עם מחזורים, טיימרים ותנאי יציאה.
- נלמד איך להריץ, לבדוק ולתקן תקלות.

כל חמש הדוגמאות כבר נמצאות בפרויקט, מוכנות לבנייה, בתיקייה:
[`TeamCode/.../teamcode/training/guide/`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/)

### 1.2 ⛔ למה לא נוגעים באף תיקייה מחוץ ל־TeamCode

פרויקט FTC בנוי משכבות. רק שכבה אחת שייכת לנו:

| תיקייה / קובץ | של מי זה? | מה קורה אם משנים |
|---|---|---|
| `FtcRobotController/` | של FIRST — זו האפליקציה שרצה על ה־Control Hub | הרובוט עלול לא לעלות, ובעדכון SDK הבא השינויים יימחקו או יתנגשו |
| `build.gradle`, `build.common.gradle`, `build.dependencies.gradle`, `settings.gradle`, `gradle/` | הגדרות הבנייה של כל הפרויקט | הבנייה נשברת, או שגרסאות הספריות משתנות וכל חומרי הקורס כבר לא מתאימים |
| `MeepMeepTesting/` | כלי סימולציה נפרד | לא קשור לרובוט; שינוי רק מבלבל |
| **`TeamCode/src/main/java/...`** | **שלנו!** | **כאן כותבים הכל** ✅ |

**ומה עם Pedro?** חשוב להבין: הקוד של Pedro עצמו **בכלל לא נמצא בתיקייה שלנו**. הוא **ספרייה חיצונית** — Gradle מוריד אותו מהאינטרנט בזמן הבנייה, לפי שורה אחת בהגדרות (`com.pedropathing:ftc:2.1.2`). לכן:

- אי אפשר "לתקן" את Pedro — וגם אין צורך. משתמשים בו כמו בקופסה שחורה עם כפתורים.
- **לא משדרגים גרסה** במהלך הלמידה. הגרסה נעולה בכוונה, כדי שכל הדוגמאות והשיעורים יעבדו.
- כל ההתאמה של Pedro לרובוט שלנו נעשית בקבצים **המיועדים לכך בתוך TeamCode**:

| קובץ ב־`TeamCode/.../Libraries/pedroPathing/` | תפקיד | נוגעים? |
|---|---|---|
| `Constants.java` | כל המספרים של הרובוט: שמות מנועים, כיוונים, מסה, מהירויות, PIDF, הגדרות Pinpoint | ✅ כן — כאן מכיילים |
| `Tuning.java` | ה־OpMode שבעזרתו מודדים ומכיילים את הערכים ל־`Constants` | 🔍 מריצים, לא עורכים |
| `FollowPathCommand.java` | עוטף מסלול של Pedro כפקודה (Command) | 🔍 קוראים ולומדים |
| `Drawing.java` | ציור הרובוט והמסלול בלוח הבקרה | 🔍 לא צריך לגעת |

> 💡 **כלל אצבע:** אם הקובץ לא נמצא תחת `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`, הוא לא שלכם. גם `TeamCode/build.gradle` נשאר כמו שהוא — אל תוסיפו ספריות בלי מנטור.

### 1.3 איפה נמצא מה

```
TeamCode/src/main/java/org/firstinspires/ftc/teamcode/
├── Libraries/pedroPathing/   ← Constants, Tuning, FollowPathCommand (הקבצים המיועדים ל־Pedro)
├── Libraries/MMLib/          ← MMRobot, MMOpMode (מחזור ההפעלה של הפרויקט)
├── OpMode/Auto/BasicAuto.java← האוטונומי של שיעור 10 (הקובץ שנפרק בפרק 3)
├── SubSystems/DriveTrain.java← יוצר את ה־Follower כשהרובוט במצב AUTO
├── training/auto/            ← DemoPathFactory (המסלול של שיעור 10)
└── training/guide/           ← חמש הדוגמאות של המדריך הזה
```

---

## 2. מושגי יסוד: צירים, קואורדינטות וזוויות

לפני שכותבים קוד, צריך לדבר באותה שפה כמו Pedro. נתחיל במילון קצר, ואחר כך נצייר את המגרש.

### 2.1 מילון מושגים

| מושג | הסבר בגובה העיניים |
|---|---|
| **OpMode** | "תוכנית" שאפשר לבחור ולהריץ ב־Driver Station. יש שני סוגים: `TeleOp` (נהיגה עם שלט) ו־`Autonomous`. |
| **Autonomous** | OpMode שרץ לבד, בלי שלט. מסמנים אותו ב־`@Autonomous(name = ...)`. |
| **Pose** | "איפה הרובוט ולאן הוא מסתכל": שלושה מספרים — `x`, `y` (באינצ'ים) ו־`heading` (זווית, **ברדיאנים**). |
| **Localizer** | החלק שמודד כל הזמן איפה הרובוט. אצלנו זה **goBILDA Pinpoint** עם גלגלי אודומטריה (בשם `pinpoint` ב־Configuration). |
| **Follower** | ה"נהג" של Pedro. מקבל מסלול, יודע איפה הרובוט (דרך ה־Localizer), ומחשב כוח לכל גלגל. |
| **Path** | קטע מסלול אחד: קו ישר או עקומה, מנקודה לנקודה. |
| **PathChain** | שרשרת של כמה Path־ים שהרובוט נוסע ברצף, בלי לעצור ביניהם. |
| **PathBuilder** | "בנאי" שמרכיב PathChain צעד אחרי צעד: `addPath(...)`, אחר כך הגדרות זווית, ובסוף `build()`. |
| **BezierLine** | קו ישר בין שתי נקודות. |
| **BezierCurve** | עקומה חלקה. יש לה נקודת התחלה, נקודת סוף, ונקודות בקרה באמצע ש"מושכות" את העקומה אליהן. |
| **Heading Interpolation** | איך הזווית של הרובוט משתנה לאורך הנסיעה (פירוט ב־2.4). |
| **holdEnd** | אם `true` — בסוף המסלול הרובוט "מחזיק" את הנקודה ומתקן אם דוחפים אותו. אם `false` — משחרר. |
| **isBusy()** | שאלה ל־Follower: "אתה עדיין באמצע מסלול?" כשהתשובה `false` — הגענו. |
| **State Machine** | דרך לחלק את האוטונומי לשלבים ("מצבים"), ולעבור משלב לשלב רק כשתנאי מתקיים. |
| **Timer** | שעון עצר. משמש כדי לחכות זמן מסוים, או כדי לוותר אם משהו לוקח יותר מדי זמן (Timeout). |

### 2.2 מערכת הצירים של Pedro

Pedro מתייחס למגרש כמו לדף משבצות בגודל **144 × 144 אינץ'** (בערך 3.66 × 3.66 מטר; כל אריח במגרש הוא 24 אינץ').

```
   Y
   ▲
144┼───────────────────────────────┐
   │                               │
   │                               │
   │                               │
 72┤             ● (72, 72)        │   ← מרכז המגרש
   │           מרכז                │
   │                               │
   │                               │
  0┼───────────────────────────────┼──► X
   0              72              144
 (0,0) = פינת המגרש
```

- **ציר X** — ימינה (כשמסתכלים על המגרש כמו בתמונה למעלה).
- **ציר Y** — למעלה, לכיוון הקיר הרחוק.
- **הראשית (0, 0)** — בפינת המגרש. המרכז הוא (72, 72).
- **יחידות:** תמיד **אינצ'ים**. אם מדדתם בסנטימטרים — מחלקים ב־2.54.

> 🗺️ **כלי עזר חשוב:** ב־[Pedro Visualizer](https://visualizer.pedropathing.com/) אפשר לגרור נקודות על תמונת מגרש, לראות את המסלול ולהעתיק את הקואורדינטות. מומלץ לתכנן שם כל מסלול לפני שכותבים קוד.

### 2.3 זוויות (Heading)

```
              90° (π/2)
                 ▲
                 │
  180° (π) ◄─────●─────► 0°   ← הרובוט "מסתכל" לכיוון +X
                 │
                 ▼
            -90° (-π/2)
```

- **0°** = החזית של הרובוט פונה לכיוון **+X** (ימינה בציור).
- **זווית חיובית = סיבוב נגד כיוון השעון.** 90° = מסתכל למעלה (+Y).
- Pedro עובד **ברדיאנים**, לא במעלות! לכן תמיד כותבים `Math.toRadians(90)` ולא סתם `90`.

| מעלות | רדיאנים | ב־Java |
|---|---|---|
| 0° | 0 | `Math.toRadians(0)` |
| 90° | π/2 ≈ 1.571 | `Math.toRadians(90)` |
| 180° | π ≈ 3.142 | `Math.toRadians(180)` |
| -90° | -π/2 | `Math.toRadians(-90)` |

> ⚠️ **הטעות הכי נפוצה במדריך:** `new Pose(30, 72, 90)` — זה **90 רדיאנים** (בערך 14 סיבובים שלמים!), לא 90 מעלות. הנכון: `new Pose(30, 72, Math.toRadians(90))`.

### 2.4 ארבע דרכים לשלוט בזווית בזמן נסיעה

אחרי כל `addPath(...)` כותבים איך הזווית תתנהג **בקטע הזה**:

| פקודה | מה הרובוט עושה | מתי משתמשים |
|---|---|---|
| `.setLinearHeadingInterpolation(a, b)` | מסתובב בהדרגה מזווית `a` לזווית `b` לאורך הקטע | הכי נפוץ — "סע לשם ותגיע כשאתה מסתכל לכיוון X" |
| `.setConstantHeadingInterpolation(h)` | שומר על זווית `h` כל הדרך | נסיעה הצידה (מאקאנום), או כשהמנגנון חייב להישאר מכוון |
| `.setTangentHeadingInterpolation()` | החזית תמיד פונה לכיוון הנסיעה, כמו מכונית | עקומות ארוכות, נסיעה מהירה |
| `.setTangentHeadingInterpolation().setReversed()` | הגב פונה לכיוון הנסיעה (נוסע ברוורס) | כשהמנגנון נמצא מאחור |

> ⚠️ **שימו לב:** אם לא כותבים כלום, ברירת המחדל של Pedro היא **Tangent**. זו הסיבה הנפוצה ל"הרובוט מסתובב פתאום בהתחלה". תמיד כתבו במפורש איזו שיטה אתם רוצים.

### 2.5 קואורדינטות מגרש מול קואורדינטות יחסיות

יש שתי דרכים לבחור נקודת התחלה:

1. **יחסית** — מתחילים ב־`(0, 0, 0)` וכל הנקודות נמדדות ממיקום ההתחלה של הרובוט. ככה עושה `BasicAuto` של שיעור 10: מסלול קצר של 12 אינץ' קדימה. נוח לתרגול ראשון.
2. **קואורדינטות מגרש** — מציבים את הרובוט במקום ידוע על המגרש (למשל `(8, 72)`, צמוד לקיר באמצע) וכל הנקודות הן מיקומים אמיתיים על המגרש. **זו השיטה המומלצת לתחרות**, כי אפשר לתכנן ב־Visualizer ולמדוד נקודות בעזרת סרגל.

בדוגמאות של המדריך משתמשים בשיטה השנייה: הרובוט מתחיל ב־`(8, 72)` ופונה ל־0°, כלומר צמוד לקיר השמאלי, באמצע, ומסתכל אל תוך המגרש. **התאימו את הנקודות לעונה ולמגרש שלכם** — המיקומים כאן הם לתרגול על מגרש ריק.

---

## 3. פירוק הקובץ: איך BasicAuto עובד

הקובץ של שיעור 10 קצר, אבל הוא נשען על כמה קבצים אחרים. נעבור עליהם לפי הסדר שבו הם רצים.

### 3.1 הקובץ עצמו — `OpMode/Auto/BasicAuto.java`

```java
@Disabled
@Autonomous(name = "Training Short Line", group = "Learning")
public class BasicAuto extends MMOpMode {
    private PathChain path;
    private boolean started;

    @Override public void initialize() {
        MMRobot.getInstance().initJeruRobot().opModeType(OpModeType.AUTO).build(this);
        path = DemoPathFactory.shortLine(
                MMRobot.getInstance().driveTrain.getFollower());
    }

    @Override public void run() {
        if (!started) {
            started = true;
            MMRobot.getInstance().driveTrain.getFollower().setStartingPose(new Pose(0, 0, 0));
            schedule(new FollowPathCommand(
                    MMRobot.getInstance().driveTrain.getFollower(), path, false, 0.3));
        }
        super.run();
    }
}
```

**שורה אחרי שורה:**

1. **`@Disabled`** — מסתיר את ה־OpMode מהרשימה ב־Driver Station. זה בכוונה: אסור להריץ אוטונומי לפני ש־Pedro מכויל. מוחקים את השורה רק אחרי הכיול (פרק 5).
2. **`@Autonomous(name = "Training Short Line", group = "Learning")`** — השם שיופיע ב־Driver Station, והקבוצה שבה הוא יופיע.
3. **`extends MMOpMode`** — המחלקה יורשת את מחזור ההפעלה של הפרויקט (פירוט ב־3.4). לכן במקום `init()`/`loop()` יש כאן `initialize()`/`run()`.
4. **`initialize()` — שלב האתחול** (רץ פעם אחת כשלוחצים INIT):
   - `MMRobot.getInstance().initJeruRobot().opModeType(OpModeType.AUTO).build(this)` — בונה את כל הרובוט במצב **AUTO**. במצב הזה `DriveTrain` יוצר את ה־Follower של Pedro (ב־TeleOp הוא לא נוצר, כי לא צריך אותו).
   - `DemoPathFactory.shortLine(...)` — **בונה את המסלול** מראש, עוד לפני START. בניית מסלולים לוקחת זמן חישוב, ולכן עושים אותה באתחול.
5. **`run()` — הלולאה** (רצה שוב ושוב מ־START ועד STOP):
   - הדגל `started` דואג שהקוד בתוך ה־`if` ירוץ **רק בסיבוב הראשון**. בגרסת SolversLib שבפרויקט אין שלב `preRun`, אז זה הפתרון.
   - `setStartingPose(new Pose(0, 0, 0))` — אומרים ל־Pedro "עכשיו אתה בנקודה (0,0) ומסתכל ל־0°". מכאן והלאה כל המיקומים יחסיים לנקודה הזו.
   - `schedule(new FollowPathCommand(follower, path, false, 0.3))` — **מתזמנים פקודה** שתנסיע את הרובוט במסלול: `false` = לא להחזיק את הנקודה בסוף, `0.3` = עוצמה מקסימלית של 30% (זהיר, מתאים לתרגול).
   - `super.run()` — **קריטי!** מריץ את המחזור של `MMOpMode`: קריאת חיישנים, הרצת הפקודות ועדכון Pedro. בלי השורה הזו שום דבר לא זז.

### 3.2 בניית המסלול — `training/auto/DemoPathFactory.java`

```java
public static PathChain shortLine(Follower follower) {
    Pose start = new Pose(0, 0, 0);
    Pose end = new Pose(12, 0, 0);
    return follower.pathBuilder()
            .addPath(new BezierLine(start, end))
            .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
            .build();
}
```

- שתי נקודות: התחלה `(0, 0)` וסוף `(12, 0)` — כלומר **12 אינץ' קדימה** בכיוון +X. שתיהן עם זווית 0.
- `follower.pathBuilder()` — פותחים "בנאי" מסלולים.
- `.addPath(new BezierLine(start, end))` — מוסיפים קטע אחד: קו ישר.
- `.setLinearHeadingInterpolation(...)` — מ־0° ל־0°, כלומר הרובוט לא מסתובב.
- `.build()` — סוגרים ומקבלים `PathChain` מוכן.

> 💡 שימו לב שהמסלול נבנה בקובץ נפרד ולא בתוך ה־OpMode. זה הרגל טוב: קל לבדוק את הגאומטריה לבד, ואפשר להשתמש באותו מסלול בכמה OpModes.

### 3.3 הפקודה שמריצה את המסלול — `Libraries/pedroPathing/FollowPathCommand.java`

בפרויקט שלנו עובדים בשיטת **Command-Based** (מבוססת פקודות, דרך SolversLib). כל פעולה היא "פקודה" עם שלושה חלקים:

```java
@Override
public void initialize() {                 // רץ פעם אחת כשהפקודה מתחילה
    if (maxPower != 1.0) {
        follower.followPath(pathChain, maxPower, holdEnd);
    } else {
        follower.followPath(pathChain, holdEnd);
    }
}

@Override
public boolean isFinished() {              // נבדק בכל סיבוב: "סיימת?"
    return !follower.isBusy();
}

@Override public void end(boolean interrupted) {   // רץ פעם אחת בסוף
    if (interrupted) follower.breakFollowing();
}
```

- **`initialize`** — נותן ל־Follower את המסלול. מכאן Pedro נוהג.
- **`isFinished`** — הפקודה נגמרת כש־`isBusy()` מחזיר `false`, כלומר כש־Pedro הגיע לסוף.
- **`end`** — אם הפקודה בוטלה באמצע (למשל STOP), עוצרים את המעקב.
- בבנאי יש `addRequirements(MMRobot.getInstance().driveTrain)` — הפקודה "תופסת" את מערכת ההנעה, כדי ששתי פקודות לא ינסו לנהוג בבת אחת.

### 3.4 המחזור שמאחורי הקלעים — `MMOpMode.run()`

בכל סיבוב של הלולאה (עשרות פעמים בשנייה), `MMOpMode` עושה בדיוק את זה, לפי הסדר:

1. **קורא את כל החיישנים בבת אחת** (`pullBulkData`) — מהיר יותר מקריאה אחת־אחת.
2. **מריץ את ה־Scheduler** (`super.run()`) — כל הפקודות הפעילות מקבלות תור.
3. **מעדכן את Pedro** (`driveTrain.updateFollower()`) — Pedro מודד מיקום, מחשב תיקון ושולח כוח לגלגלים.
4. **שולח telemetry** ל־Driver Station ולוח הבקרה.

וכשהאוטונומי נגמר, `end()` מבטל את כל הפקודות, עוצר את כל המנועים ומשחרר את Pedro — גם אם הייתה שגיאה באמצע.

> 🔑 **הכלל החשוב ביותר ב־Pedro:** צריך לקרוא ל־`follower.update()` **בכל סיבוב של הלולאה**. ב־`MMOpMode` זה קורה אוטומטית. בדוגמאות של המדריך (פרק 4) נכתוב את זה בעצמנו — וזו השורה הראשונה ב־`loop()`.

### 3.5 הכיול — `Libraries/pedroPathing/Constants.java`

הקובץ הזה הוא "תעודת הזהות" של הרובוט מבחינת Pedro. הוא מחולק לארבעה חלקים:

| חלק | מה יש בו | איך ממלאים |
|---|---|---|
| `followerConstants` | מסה (`mass`), תאוטה כשמשחררים את המנועים (`ZeroPowerAcceleration`), מקדמי PIDF לתיקון מיקום, זווית ונסיעה | מריצים את הכלים ב־`Tuning` |
| `driveConstants` | שמות המנועים (`FL`, `FR`, `BL`, `BR`), הכיוונים שלהם, מהירות מקסימלית בכל ציר | שמות — לפי ה־Configuration; מהירויות — מ־`Tuning` |
| `localizerConstants` | הגדרות ה־Pinpoint: שם (`pinpoint`), מיקום גלגלי האודומטריה ביחס למרכז הרובוט, כיוון הספירה | מודדים ברובוט + `Offsets Tuner` |
| `pathConstraints` | מתי מסלול נחשב "גמור": `PathConstraints(0.99, 100, 1, 1)` — אחוז השלמה, Timeout בסוף (מילישניות), ועוצמת הבלימה | בדרך כלל משאירים |

והפונקציה `createFollower(hardwareMap)` מרכיבה מכל אלה Follower אחד. גם `DriveTrain` של הפרויקט וגם הדוגמאות שלנו יוצרים את ה־Follower דרכה, כך שכל הכיול נמצא במקום אחד.

### 3.6 איפה מכונת המצבים?

ב־`BasicAuto` יש רק פעולה אחת, אז אין צורך במכונת מצבים. כשיש כמה שלבים, יש שתי דרכים לבנות אותה:

**דרך א' — Command-Based (הסגנון של הפרויקט):** משרשרים פקודות. ה־Scheduler הוא בעצם מכונת המצבים. זה נראה בערך כך (סקיצה להמחשה):

```java
schedule(new SequentialCommandGroup(
        new FollowPathCommand(follower, toPickup),      // שלב 1: נסיעה
        new InstantCommand(() -> claw.setPosition(0.45)),// שלב 2: סגירת תופסן
        new WaitCommand(500),                           // שלב 3: המתנה חצי שנייה
        new FollowPathCommand(follower, toScore)        // שלב 4: נסיעה ליעד
));
```

**דרך ב' — OpMode רגיל עם `switch` (הסגנון של הדוגמאות):** כותבים בעצמנו משתנה `pathState`, ובכל סיבוב בודקים "באיזה מצב אנחנו? האם התנאי למעבר התקיים?".

במדריך נשתמש **בדרך ב'**, כי בה רואים בדיוק מה קורה בכל רגע — וזה הכי טוב ללמידה. אחרי שמבינים אותה, הדרך הראשונה נראית פשוטה מאוד.

> ⚠️ **לא מערבבים בין השיטות באותו OpMode.** הדוגמאות יוצרות Follower משלהן דרך `Constants.createFollower`. אם תבנו באותו OpMode גם את `MMRobot` במצב AUTO, ייווצרו **שני** Followers שינסו לנהוג באותם מנועים.

---

## 4. חמש דוגמאות מלאות מקצה לקצה

לכל הדוגמאות אותו שלד. כדאי להכיר אותו פעם אחת:

| מתודה | מתי רצה | מה עושים בה |
|---|---|---|
| `init()` | פעם אחת, כשלוחצים INIT | יוצרים Follower, קובעים נקודת התחלה, מחברים חומרה, **בונים את כל המסלולים** |
| `init_loop()` | שוב ושוב, עד START | `follower.update()` ו־telemetry, כדי לראות שהמיקום הגיוני |
| `start()` | פעם אחת, כשלוחצים START | מאפסים טיימרים, קובעים מצב ראשון, מדליקים מנגנונים |
| `loop()` | שוב ושוב, עד STOP | **קודם כל `follower.update()`**, אחר כך מכונת המצבים, ובסוף telemetry |
| `stop()` | פעם אחת, כשלוחצים STOP או כשהזמן נגמר | עוצרים את Pedro ואת המנגנונים |

> 📌 **חוק הזהב:** בתוך `loop()` לעולם לא כותבים `while` או `sleep`. הלולאה צריכה להסתיים מהר ולחזור, כדי ש־`follower.update()` ירוץ עשרות פעמים בשנייה. במקום לחכות — שומרים מצב ובודקים טיימר.

כל הדוגמאות מסומנות `@Disabled` ומופיעות בקבוצה `Pedro Guide`. כדי להריץ — מוחקים את `@Disabled` (רק אחרי כיול!).


### דוגמה 1 — תנועה ליניארית בסיסית

**המטרה:** לנסוע 24 אינץ' ישר קדימה ולעצור.
**מה חדש כאן:** השלד המלא של אוטונומי עם Pedro, ומכונת המצבים הכי פשוטה שיש.

```
 Y=72   S ●━━━━━━━━━━━━━━━━━━► E
      (8,72)                (32,72)
```

**קובץ:** [`training/guide/GuideAuto1StraightLine.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/GuideAuto1StraightLine.java)

```java
package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 1: נסיעה ישרה מנקודה א' לנקודה ב' ועצירה.
 * מחקו את @Disabled רק אחרי כיול Pedro ובדיקה שהשטח לפני הרובוט פנוי.
 */
@Disabled
@Autonomous(name = "Guide 1 - Straight Line", group = "Pedro Guide")
public class GuideAuto1StraightLine extends OpMode {

    // ---- נקודות (אינצ'ים, זווית ברדיאנים) ----
    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));   // צמוד לקיר, פונה לתוך המגרש
    private final Pose endPose   = new Pose(32, 72, Math.toRadians(0));  // 24 אינץ' קדימה

    // ---- אובייקטים ----
    private Follower follower;
    private PathChain driveForward;
    private Timer opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
        opModeTimer = new Timer();
    }

    private void buildPaths() {
        driveForward = follower.pathBuilder()
                .addPath(new BezierLine(startPose, endPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), endPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update(); // מעדכן מיקום בלבד; אין מסלול פעיל ולכן אין תנועה
        telemetry.addLine("Guide 1 ready. Press START.");
        addPoseTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        pathState = 0;
    }

    @Override
    public void loop() {
        follower.update();          // חובה בכל סיבוב!
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    /** מכונת מצבים מינימלית: מתחילים מסלול, מחכים לסיום, מסיימים. */
    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(driveForward, true); // true = להחזיק את הנקודה בסוף
                pathState = 1;
                break;
            case 1:
                if (!follower.isBusy()) {
                    pathState = -1; // סיימנו
                }
                break;
            default:
                // -1: לא עושים כלום. Pedro ממשיך להחזיק את הרובוט בנקודה.
                break;
        }
    }

    private void addPoseTelemetry() {
        Pose p = follower.getPose();
        telemetry.addData("X", "%.1f", p.getX());
        telemetry.addData("Y", "%.1f", p.getY());
        telemetry.addData("Heading [deg]", "%.1f", Math.toDegrees(p.getHeading()));
    }

    @Override
    public void stop() {
        follower.breakFollowing();
    }
}
```

**מה קורה כאן:**

- **`init()`** — יוצרים Follower מתוך `Constants`, אומרים לו איפה הרובוט עומד (`setStartingPose`), ובונים את המסלול.
- **`buildPaths()`** — קו ישר אחד (`BezierLine`) מ־`startPose` ל־`endPose`. הזווית נשארת 0°.
- **`loop()`** — `follower.update()` בשורה הראשונה, ואז מכונת המצבים:
  - **מצב 0:** מתחילים לנסוע (`followPath`) ועוברים מיד למצב 1. שימו לב: `followPath` רק **מתחיל** את הנסיעה ולא מחכה לסופה.
  - **מצב 1:** בודקים בכל סיבוב אם `isBusy()` הפך ל־`false`. כשכן — עוברים ל־`-1` (סיום).
- בזכות `holdEnd = true`, גם אחרי הסיום Pedro ממשיך להחזיק את הרובוט בנקודה.

**🏋️ תרגיל:** שנו את `endPose` כך שהרובוט ייסע 24 אינץ' **הצידה** (בכיוון +Y) בלי להסתובב. איזה ערך של `x` ו־`y` צריך?

---

### דוגמה 2 — תנועה עם שינוי זווית (Heading Control)

**המטרה:** שלושה קטעים, ובכל אחד שיטה אחרת לשליטה בזווית.
**מה חדש כאן:** שלוש שיטות Heading, טיימר בין מצבים, ופונקציה `setPathState` שמאפסת את הטיימר בכל מעבר.

```
 Y=96        B ●━━━━━━━━━━━► ● C
              ↗ (32,96)       ┃ (56,96)
             ↗  90°           ┃  נוסע אחורה,
            ↗                 ┃  מסתכל ל־90°
 Y=72  S ●                    ▼ ● (56,72)
      (8,72) 0°
   A: 0°→90° בהדרגה   B: נשאר על 90° (הצידה)   C: Tangent + Reversed
```

**קובץ:** [`training/guide/GuideAuto2HeadingControl.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/GuideAuto2HeadingControl.java)

```java
package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 2: שליטה בזווית (Heading Control).
 * שלושה קטעים, ובכל אחד שיטה אחרת לקביעת הזווית:
 *   A. Linear  — הזווית משתנה בהדרגה מ־0° ל־90° במהלך הנסיעה.
 *   B. Constant — הרובוט שומר על 90° ונוסע הצידה (מאקאנום).
 *   C. Tangent + Reversed — הרובוט נוסע "ברוורס", הגב שלו פונה לכיוון התנועה.
 */
@Disabled
@Autonomous(name = "Guide 2 - Heading Control", group = "Pedro Guide")
public class GuideAuto2HeadingControl extends OpMode {

    private static final double PAUSE_SECONDS = 0.5; // הפסקה קצרה בין קטעים, כדי שיהיה קל לראות

    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));
    private final Pose turnPose  = new Pose(32, 96, Math.toRadians(90));
    private final Pose sidePose  = new Pose(56, 96, Math.toRadians(90));
    private final Pose backPose  = new Pose(56, 72, Math.toRadians(90));

    private Follower follower;
    private PathChain linearTurn, constantStrafe, reverseTangent;
    private Timer pathTimer, opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        pathTimer = new Timer();
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        // A: נוסעים באלכסון ומסתובבים בהדרגה מ־0° ל־90°
        linearTurn = follower.pathBuilder()
                .addPath(new BezierLine(startPose, turnPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), turnPose.getHeading())
                .build();

        // B: נוסעים ימינה (בכיוון +X) אבל החזית נשארת על 90°
        constantStrafe = follower.pathBuilder()
                .addPath(new BezierLine(turnPose, sidePose))
                .setConstantHeadingInterpolation(sidePose.getHeading())
                .build();

        // C: נוסעים "למטה" (בכיוון -Y). Tangent לבד היה מסובב את החזית ל־(-90°).
        //    setReversed הופך אותה, כך שהרובוט נשאר על 90° ונוסע אחורה.
        reverseTangent = follower.pathBuilder()
                .addPath(new BezierLine(sidePose, backPose))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 2 ready. Press START.");
        addPoseTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // קטע A
                follower.followPath(linearTurn, true);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) setPathState(2);
                break;
            case 2: // הפסקה, ואז קטע B
                if (pathTimer.getElapsedTimeSeconds() > PAUSE_SECONDS) {
                    follower.followPath(constantStrafe, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) setPathState(4);
                break;
            case 4: // הפסקה, ואז קטע C
                if (pathTimer.getElapsedTimeSeconds() > PAUSE_SECONDS) {
                    follower.followPath(reverseTangent, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) setPathState(-1);
                break;
            default:
                break;
        }
    }

    /** מחליפים מצב ומאפסים את הטיימר של המצב — תמיד דרך הפונקציה הזו. */
    private void setPathState(int newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    private void addPoseTelemetry() {
        Pose p = follower.getPose();
        telemetry.addData("X", "%.1f", p.getX());
        telemetry.addData("Y", "%.1f", p.getY());
        telemetry.addData("Heading [deg]", "%.1f", Math.toDegrees(p.getHeading()));
    }

    @Override
    public void stop() {
        follower.breakFollowing();
    }
}
```

**מה קורה כאן:**

- **קטע A (`setLinearHeadingInterpolation`)** — הרובוט נוסע באלכסון, ובאותו זמן מסתובב בהדרגה מ־0° ל־90°. כשהוא מגיע, הוא כבר מסתכל "למעלה".
- **קטע B (`setConstantHeadingInterpolation`)** — הרובוט נוסע ימינה, אבל החזית נשארת על 90°. זה אפשרי רק ברובוט עם גלגלי מאקאנום, שיודע לנסוע הצידה.
- **קטע C (`setTangentHeadingInterpolation().setReversed()`)** — הרובוט נוסע למטה (כיוון -Y). Tangent לבד היה מסובב את החזית לכיוון הנסיעה (-90°), כלומר סיבוב של חצי סיבוב. `setReversed()` הופך את זה: הגב פונה לכיוון הנסיעה, והרובוט נשאר על 90°.
- **`setPathState(...)`** — כל מעבר מצב עובר דרך פונקציה אחת שגם מאפסת את `pathTimer`. כך במצבים 2 ו־4 אפשר לשאול "עברו כבר חצי שנייה מאז שנכנסנו למצב?".

**🏋️ תרגיל:** הוסיפו קטע רביעי שמחזיר את הרובוט ל־`startPose` ומסובב אותו בחזרה ל־0°. איזו שיטת Heading הכי מתאימה?

---

### דוגמה 3 — מסלול עקום ורב־שלבי (Bezier Curve / Multi-segment)

**המטרה:** לעקוף מכשול בקשת, לנסוע ישר, ולהיכנס בפנייה אל נקודת הסיום — הכל ברצף, בלי לעצור.
**מה חדש כאן:** `BezierCurve` עם נקודת בקרה, ו־`PathChain` עם שלושה קטעים.

```
 Y=110          ○ arcControl (30,110)
 Y=100        ╭━━━━━━━● ━━━━━━━━━━━● ╮   ○ cornerControl (124,100)
             ╱   pastObstacle   straightEnd ╲
            ╱       (60,100)    (96,100)     ┃
 Y=72  S ● ╱   ▓▓ מכשול ▓▓                   ● E (120,72), -90°
      (8,72)     (~40,72)
```

**איך עובדת עקומת Bezier?** נקודת הבקרה היא כמו מגנט: העקומה לא עוברת דרכה, אבל נמשכת אליה. ככל שהנקודה רחוקה יותר — הקשת "בולטת" יותר. נסו להזיז את `arcControl` ב־[Visualizer](https://visualizer.pedropathing.com/) ותראו מיד את ההשפעה.

**קובץ:** [`training/guide/GuideAuto3CurvedPath.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/GuideAuto3CurvedPath.java)

```java
package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 3: מסלול עקום ורב־שלבי (Bezier Curve + PathChain).
 * המכשול (דמיוני) נמצא בערך ב־(40, 72). הרובוט עוקף אותו מלמעלה בקשת,
 * נוסע ישר, ואז מסתובב בקשת נוספת אל נקודת הסיום — הכל ברצף אחד בלי לעצור.
 */
@Disabled
@Autonomous(name = "Guide 3 - Curved Multi-Segment", group = "Pedro Guide")
public class GuideAuto3CurvedPath extends OpMode {

    // ---- נקודות ציון (Waypoints) ----
    private final Pose startPose  = new Pose(8, 72, Math.toRadians(0));
    private final Pose arcControl = new Pose(30, 110);                   // נקודת בקרה: "מושכת" את הקשת למעלה
    private final Pose pastObstacle = new Pose(60, 100, Math.toRadians(0));
    private final Pose straightEnd  = new Pose(96, 100, Math.toRadians(0));
    private final Pose cornerControl = new Pose(124, 100);              // נקודת בקרה לפנייה
    private final Pose endPose    = new Pose(120, 72, Math.toRadians(-90));

    private Follower follower;
    private PathChain obstacleRun;
    private Timer opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        obstacleRun = follower.pathBuilder()
                // קטע 1: קשת שעוקפת את המכשול. הרובוט ממשיך לפנות קדימה (0°)
                .addPath(new BezierCurve(startPose, arcControl, pastObstacle))
                .setConstantHeadingInterpolation(Math.toRadians(0))

                // קטע 2: קו ישר. Tangent = החזית פונה לכיוון הנסיעה (כאן +X, כלומר 0°)
                .addPath(new BezierLine(pastObstacle, straightEnd))
                .setTangentHeadingInterpolation()

                // קטע 3: פנייה בקשת אל נקודת הסיום, תוך סיבוב הדרגתי מ־0° ל־(-90°)
                .addPath(new BezierCurve(straightEnd, cornerControl, endPose))
                .setLinearHeadingInterpolation(straightEnd.getHeading(), endPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 3 ready. Press START.");
        addPoseTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        pathState = 0;
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Segment (0-2)", follower.getChainIndex());
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(obstacleRun, true);
                pathState = 1;
                break;
            case 1:
                // isBusy נשאר true לאורך כל השרשרת, ונהיה false רק בסוף הקטע האחרון
                if (!follower.isBusy()) pathState = -1;
                break;
            default:
                break;
        }
    }

    private void addPoseTelemetry() {
        Pose p = follower.getPose();
        telemetry.addData("X", "%.1f", p.getX());
        telemetry.addData("Y", "%.1f", p.getY());
        telemetry.addData("Heading [deg]", "%.1f", Math.toDegrees(p.getHeading()));
    }

    @Override
    public void stop() {
        follower.breakFollowing();
    }
}
```

**מה קורה כאן:**

- **שרשרת אחת, שלושה קטעים.** כל `addPath(...)` מוסיף קטע, והגדרת ה־Heading שאחריו חלה **רק על הקטע הזה**.
- **חוק הרציפות:** נקודת הסוף של כל קטע חייבת להיות בדיוק נקודת ההתחלה של הקטע הבא (`pastObstacle`, `straightEnd`). אחרת הרובוט "יקפוץ" בין הקטעים.
- **הזוויות מתחברות חלק:** קטע 1 נגמר ב־0°, קטע 2 (Tangent, נסיעה ב־+X) גם הוא 0°, וקטע 3 מתחיל ב־0° ומסתיים ב־(-90°). אין סיבובים פתאומיים.
- **`follower.getChainIndex()`** ב־telemetry מראה באיזה קטע הרובוט נמצא (0, 1 או 2). מאוד שימושי לדיבאגינג.
- **`isBusy()`** נשאר `true` לאורך כל השרשרת, ונהיה `false` רק בסוף הקטע האחרון.

**🏋️ תרגיל:** הוסיפו עוד נקודת בקרה לקטע הראשון (`BezierCurve` מקבלת כמה נקודות שרוצים). מה קורה לצורת הקשת?

---

### דוגמה 4 — אוטונומי מבוסס מכונת מצבים (FSM) עם מנגנון

**המטרה:** נסיעה לאיסוף → סגירת תופסן → הרמת מעלית → נסיעה לניקוד → פתיחת תופסן → הורדת מעלית וחנייה.
**מה חדש כאן:** `enum` למצבים (שמות במקום מספרים), שליטה במנוע ובסרבו, המתנה למנגנון, ו־Timeout כרשת ביטחון.

```
                     SCORE ●(30,110) 90°  ◄━━ PARK ●(12,110)
                           ▲
                           ┃ נסיעה עם המעלית למעלה
 Y=72  S ●━━━━━━━━━━━━━━━► ● PICKUP (30,72)
      (8,72)                 סגירת תופסן + הרמה
```

**חומרה:** מנוע בשם `lift` וסרבו בשם `claw` — אותם שמות כמו בשיעורי המעלית והסרבו בפרויקט. אם השמות שלכם שונים, שנו אותם **גם בקוד וגם ב־Robot Configuration**.

**קובץ:** [`training/guide/GuideAuto4StateMachine.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/GuideAuto4StateMachine.java)

```java
package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 4: מכונת מצבים (FSM) עם מנגנון.
 * נוסעים לאיסוף -> סוגרים תופסן -> מרימים מעלית -> נוסעים לניקוד -> פותחים -> מורידים וחונים.
 *
 * חומרה (אותם שמות כמו בשיעורי הפרויקט): מנוע "lift", סרבו "claw".
 * ⚠️ כל הקבועים של המנגנון הם ערכי התחלה זהירים. מדדו וכיילו לרובוט שלכם!
 */
@Disabled
@Autonomous(name = "Guide 4 - State Machine + Mechanism", group = "Pedro Guide")
public class GuideAuto4StateMachine extends OpMode {

    // ---- קבועי מנגנון (לכייל!) ----
    private static final int    LIFT_DOWN_TICKS   = 0;
    private static final int    LIFT_SCORE_TICKS  = 300;   // החליפו בגובה שמדדתם
    private static final int    LIFT_TOLERANCE    = 15;    // "קרוב מספיק" בטיקים
    private static final double LIFT_POWER        = 0.6;
    private static final double LIFT_TIMEOUT_SEC  = 2.0;   // רשת ביטחון אם המעלית לא מגיעה
    private static final double CLAW_OPEN         = 0.55;  // טווח זהיר כמו ב־ServoTuning
    private static final double CLAW_CLOSED       = 0.45;
    private static final double CLAW_MOVE_SEC     = 0.5;   // זמן שהסרבו צריך כדי לזוז

    // ---- נקודות ----
    private final Pose startPose  = new Pose(8, 72, Math.toRadians(0));
    private final Pose pickupPose = new Pose(30, 72, Math.toRadians(0));
    private final Pose scorePose  = new Pose(30, 110, Math.toRadians(90));
    private final Pose parkPose   = new Pose(12, 110, Math.toRadians(90));

    // ---- המצבים של האוטונומי ----
    private enum State {
        DRIVE_TO_PICKUP, WAIT_AT_PICKUP, GRAB, LIFT_UP,
        DRIVE_TO_SCORE, RELEASE, LOWER_AND_PARK, DONE
    }

    private Follower follower;
    private DcMotorEx lift;
    private Servo claw;
    private PathChain toPickup, toScore, toPark;
    private Timer stateTimer, opModeTimer;
    private State state;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        lift = hardwareMap.get(DcMotorEx.class, "lift");
        lift.setDirection(DcMotorSimple.Direction.FORWARD);      // בדקו כיוון!
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  // המעלית חייבת להתחיל למטה
        lift.setTargetPosition(LIFT_DOWN_TICKS);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0);                                      // לא זזים לפני START

        claw = hardwareMap.get(Servo.class, "claw");

        stateTimer = new Timer();
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        toPickup = follower.pathBuilder()
                .addPath(new BezierLine(startPose, pickupPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), pickupPose.getHeading())
                .build();
        toScore = follower.pathBuilder()
                .addPath(new BezierLine(pickupPose, scorePose))
                .setLinearHeadingInterpolation(pickupPose.getHeading(), scorePose.getHeading())
                .build();
        toPark = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, parkPose))
                .setConstantHeadingInterpolation(parkPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 4 ready. Lift must start at the bottom!");
        telemetry.addData("Lift ticks", lift.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        claw.setPosition(CLAW_OPEN);
        lift.setPower(LIFT_POWER);
        setState(State.DRIVE_TO_PICKUP);
    }

    @Override
    public void loop() {
        follower.update();
        updateStateMachine();

        telemetry.addData("State", state);
        telemetry.addData("State time [s]", "%.1f", stateTimer.getElapsedTimeSeconds());
        telemetry.addData("Lift ticks / target", "%d / %d",
                lift.getCurrentPosition(), lift.getTargetPosition());
        Pose p = follower.getPose();
        telemetry.addData("Pose", "(%.1f, %.1f) %.0f°", p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
        telemetry.update();
    }

    private void updateStateMachine() {
        switch (state) {
            case DRIVE_TO_PICKUP:
                follower.followPath(toPickup, true);
                setState(State.WAIT_AT_PICKUP);
                break;

            case WAIT_AT_PICKUP:
                if (!follower.isBusy()) {
                    claw.setPosition(CLAW_CLOSED);       // תופסים
                    setState(State.GRAB);
                }
                break;

            case GRAB:
                if (stateTimer.getElapsedTimeSeconds() > CLAW_MOVE_SEC) {
                    lift.setTargetPosition(LIFT_SCORE_TICKS); // מרימים
                    setState(State.LIFT_UP);
                }
                break;

            case LIFT_UP:
                // ממשיכים כשהמעלית הגיעה — או אחרי Timeout, כדי לא להיתקע לנצח
                if (liftAtTarget() || stateTimer.getElapsedTimeSeconds() > LIFT_TIMEOUT_SEC) {
                    follower.followPath(toScore, true);
                    setState(State.DRIVE_TO_SCORE);
                }
                break;

            case DRIVE_TO_SCORE:
                if (!follower.isBusy()) {
                    claw.setPosition(CLAW_OPEN);         // משחררים
                    setState(State.RELEASE);
                }
                break;

            case RELEASE:
                if (stateTimer.getElapsedTimeSeconds() > CLAW_MOVE_SEC) {
                    lift.setTargetPosition(LIFT_DOWN_TICKS);
                    follower.followPath(toPark, true);   // מורידים וחונים במקביל
                    setState(State.LOWER_AND_PARK);
                }
                break;

            case LOWER_AND_PARK:
                if (!follower.isBusy() && (liftAtTarget()
                        || stateTimer.getElapsedTimeSeconds() > LIFT_TIMEOUT_SEC)) {
                    setState(State.DONE);
                }
                break;

            case DONE:
            default:
                break;
        }
    }

    private boolean liftAtTarget() {
        return Math.abs(lift.getCurrentPosition() - lift.getTargetPosition()) <= LIFT_TOLERANCE;
    }

    private void setState(State newState) {
        state = newState;
        stateTimer.resetTimer();
    }

    @Override
    public void stop() {
        follower.breakFollowing();
        lift.setPower(0);
    }
}
```

**מה קורה כאן:**

- **`enum State`** — במקום לזכור ש"מצב 3 זה הרמת מעלית", המצבים מקבלים שמות. ב־telemetry רואים `LIFT_UP` ולא `3`, וקשה הרבה יותר להתבלבל.
- **המעלית במצב `RUN_TO_POSITION`** — נותנים לה יעד בטיקים (`setTargetPosition`) והבקר של המנוע מגיע אליו לבד. ב־`init()` העוצמה היא 0, כדי שלא תזוז לפני START (זה גם חוק בתחרות). ב־`start()` נותנים עוצמה.
- **המתנה לסרבו (`GRAB`, `RELEASE`)** — לסרבו אין חיישן שאומר "הגעתי", אז מחכים זמן קבוע (`CLAW_MOVE_SEC`) שמדדנו.
- **המתנה למעלית (`LIFT_UP`)** — ממשיכים כש־`liftAtTarget()` מחזיר `true`, **או** כשעבר `LIFT_TIMEOUT_SEC`. בלי ה־Timeout, אם המעלית נתקעת 5 טיקים מתחת ליעד — האוטונומי נתקע לנצח.
- **פעולות במקביל (`RELEASE` → `LOWER_AND_PARK`)** — מורידים את המעלית **ובאותו רגע** מתחילים לנסוע לחנייה. ה־FSM לא חייב לעשות דבר אחד בכל פעם. במצב `LOWER_AND_PARK` מחכים ששני התנאים יתקיימו.
- **ערכי הכיול** — `LIFT_SCORE_TICKS = 300` ו־`CLAW_OPEN/CLOSED = 0.55/0.45` הם ערכי התחלה זהירים (בדיוק כמו ב־`ElevatorTuning` ו־`ServoTuning` של הפרויקט). מודדים את הערכים האמיתיים בשיעורי המעלית והסרבו, ורק אז מעדכנים.

**🏋️ תרגיל:** הוסיפו מצב `CHECK_GRAB`: אחרי הסגירה, אם חיישן מגע (`liftBottom` מהשיעור, או חיישן אחר) לא מזהה שהחפץ נתפס — פותחים, מנסים שוב פעם אחת, ואם שוב לא — מדלגים ישר לחנייה.

---

### דוגמה 5 — אוטונומי מורכב עם תזמונים ותנאי יציאה

**המטרה:** שני מחזורי "אסוף ונקד", עם הגנות מכל הכיוונים:

| טכניקה | איפה בקוד | למה זה חשוב |
|---|---|---|
| **פעולה אסינכרונית** | `addParametricCallback(0.5, ...)` | ה־intake נדלק באמצע הנסיעה, בלי לעצור. חוסך זמן יקר. |
| **סף הגעה (Threshold)** | `arrived()` | ממשיכים כשהרובוט במרחק 2 אינץ' מהיעד, בלי לחכות שיעצור לגמרי. |
| **Timeout לנסיעה** | `timedOut()` | אם נסיעה לוקחת יותר מ־4 שניות, או ש־Pedro מזהה שהרובוט תקוע — מוותרים וממשיכים. |
| **המתנה מבוססת טיימר** | `COLLECTING`, `SCORING` | אוספים שנייה, מנקדים 0.8 שנייה — בלי `sleep`. |
| **מסלול דינמי** | `pathFromHereTo()` | כל מסלול נבנה בזמן ריצה **מהמיקום האמיתי** של הרובוט, גם אם קודם היה Timeout והוא לא במקום המתוכנן. |
| **שעון כללי** | בתחילת `loop()` | אחרי 25 שניות — עוזבים הכל ונוסעים לחנייה, לא משנה באיזה מצב. |
| **החלטה דינמית** | `SCORING` | מתחילים מחזור נוסף רק אם נשארו נקודות **וגם** נשאר מספיק זמן. |

**חומרה:** מנועים בשם `intake` ו־`transfer` (כמו בשיעור Command Groups).

**קובץ:** [`training/guide/GuideAuto5TimedCycles.java`](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/training/guide/GuideAuto5TimedCycles.java)

```java
package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 5: אוטונומי מורכב עם תזמונים ותנאי יציאה.
 * - פעולה אסינכרונית: ה־intake נדלק באמצע הנסיעה (Parametric Callback).
 * - Threshold: ממשיכים כשהרובוט "קרוב מספיק", בלי לחכות לעצירה מלאה.
 * - Timeouts: אם נסיעה לוקחת יותר מדי זמן או שהרובוט תקוע — מוותרים וממשיכים.
 * - מסלול דינמי: כל מסלול נבנה בזמן ריצה מהמיקום האמיתי של הרובוט.
 * - שעון כללי: כשמתקרבים לסוף 30 השניות — עוזבים הכל ונוסעים לחנייה.
 *
 * חומרה: מנועים "intake" ו־"transfer" (כמו בשיעור Command Groups).
 */
@Disabled
@Autonomous(name = "Guide 5 - Timed Cycles", group = "Pedro Guide")
public class GuideAuto5TimedCycles extends OpMode {

    // ---- זמנים (שניות) ----
    private static final double PATH_TIMEOUT_SEC     = 4.0;  // נסיעה אחת לא אמורה לקחת יותר
    private static final double COLLECT_SEC          = 1.0;  // כמה זמן אוספים בנקודה
    private static final double SCORE_SEC            = 0.8;  // כמה זמן מפעילים transfer
    private static final double LAST_CYCLE_START_SEC = 18.0; // אחרי זה לא מתחילים מחזור חדש
    private static final double PARK_DEADLINE_SEC    = 25.0; // אחרי זה — ישר לחנייה

    // ---- ספים ועוצמות ----
    private static final double ARRIVE_THRESHOLD_IN  = 2.0;  // "הגענו" אם המרחק קטן מזה (אינץ')
    private static final double MIN_PATH_LENGTH_IN   = 1.0;  // לא בונים מסלול קצר מזה
    private static final double INTAKE_POWER         = 1.0;
    private static final double TRANSFER_POWER       = 1.0;

    // ---- נקודות ----
    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));
    private final Pose scorePose = new Pose(14, 72, Math.toRadians(0));
    private final Pose parkPose  = new Pose(40, 72, Math.toRadians(0));
    private final Pose[] collectPoses = {
            new Pose(36, 96, Math.toRadians(0)),
            new Pose(36, 48, Math.toRadians(0)),
    };

    private enum State {
        START_CYCLE, DRIVING_TO_COLLECT, COLLECTING,
        GO_SCORE, DRIVING_TO_SCORE, SCORING,
        GO_PARK, PARKING, DONE
    }

    private Follower follower;
    private DcMotor intake, transfer;
    private Timer stateTimer, opModeTimer;
    private State state;
    private int cycle;
    private int timeouts;          // כמה פעמים ויתרנו — מצוין לדיבאגינג
    private Pose currentTarget;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        intake.setPower(0);
        transfer.setPower(0);

        stateTimer = new Timer();
        opModeTimer = new Timer();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 5 ready. Press START.");
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        cycle = 0;
        timeouts = 0;
        setState(State.START_CYCLE);
    }

    @Override
    public void loop() {
        follower.update();

        // שעון כללי: מעל הכל. לא משנה איפה אנחנו — כשהזמן נגמר, חונים.
        if (opModeTimer.getElapsedTimeSeconds() > PARK_DEADLINE_SEC
                && state != State.GO_PARK && state != State.PARKING && state != State.DONE) {
            stopMechanisms();
            setState(State.GO_PARK);
        }

        updateStateMachine();

        telemetry.addData("State", state);
        telemetry.addData("Cycle", "%d / %d", cycle, collectPoses.length);
        telemetry.addData("Timeouts", timeouts);
        telemetry.addData("Auto time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        telemetry.addData("State time [s]", "%.1f", stateTimer.getElapsedTimeSeconds());
        Pose p = follower.getPose();
        telemetry.addData("Pose", "(%.1f, %.1f) %.0f°", p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
        if (currentTarget != null) {
            telemetry.addData("Distance to target [in]", "%.1f", p.distanceFrom(currentTarget));
        }
        telemetry.update();
    }

    private void updateStateMachine() {
        switch (state) {
            case START_CYCLE: {
                currentTarget = collectPoses[cycle];
                // ה־intake נדלק כשעוברים 50% מהמסלול — בזמן שהרובוט עדיין נוסע
                PathChain path = pathFromHereTo(currentTarget, 0.5,
                        () -> intake.setPower(INTAKE_POWER));
                if (path != null) follower.followPath(path, true);
                else intake.setPower(INTAKE_POWER); // כבר שם
                setState(State.DRIVING_TO_COLLECT);
                break;
            }

            case DRIVING_TO_COLLECT:
                if (arrived(currentTarget)) {
                    intake.setPower(INTAKE_POWER);   // ליתר ביטחון, אם עקפנו את ה־Callback
                    setState(State.COLLECTING);
                } else if (timedOut()) {
                    timeouts++;
                    follower.breakFollowing();
                    intake.setPower(0);
                    setState(State.GO_SCORE);         // מוותרים על האיסוף, ממשיכים הלאה
                }
                break;

            case COLLECTING:
                if (stateTimer.getElapsedTimeSeconds() > COLLECT_SEC) {
                    intake.setPower(0);
                    setState(State.GO_SCORE);
                }
                break;

            case GO_SCORE: {
                currentTarget = scorePose;
                PathChain path = pathFromHereTo(scorePose, -1, null);
                if (path != null) follower.followPath(path, true);
                setState(State.DRIVING_TO_SCORE);
                break;
            }

            case DRIVING_TO_SCORE:
                if (!follower.isBusy() || timedOut()) {
                    if (follower.isBusy()) {          // יצאנו בגלל Timeout
                        timeouts++;
                        follower.breakFollowing();
                    }
                    transfer.setPower(TRANSFER_POWER);
                    setState(State.SCORING);
                }
                break;

            case SCORING:
                if (stateTimer.getElapsedTimeSeconds() > SCORE_SEC) {
                    transfer.setPower(0);
                    cycle++;
                    boolean morePoses = cycle < collectPoses.length;
                    boolean enoughTime = opModeTimer.getElapsedTimeSeconds() < LAST_CYCLE_START_SEC;
                    setState(morePoses && enoughTime ? State.START_CYCLE : State.GO_PARK);
                }
                break;

            case GO_PARK: {
                currentTarget = parkPose;
                PathChain path = pathFromHereTo(parkPose, -1, null);
                if (path != null) follower.followPath(path, true);
                setState(State.PARKING);
                break;
            }

            case PARKING:
                if (!follower.isBusy()) setState(State.DONE);
                break;

            case DONE:
            default:
                break;
        }
    }

    /**
     * בונה מסלול ישר מהמיקום הנוכחי אל target.
     * callbackT בין 0 ל־1 מפעיל את action באמצע הדרך; ערך שלילי = בלי Callback.
     * מחזיר null אם הרובוט כבר כמעט ביעד (מסלול באורך 0 עלול לגרום לשגיאות).
     */
    private PathChain pathFromHereTo(Pose target, double callbackT, Runnable action) {
        Pose here = follower.getPose();
        if (here.distanceFrom(target) < MIN_PATH_LENGTH_IN) return null;

        if (callbackT >= 0 && action != null) {
            return follower.pathBuilder()
                    .addPath(new BezierLine(here, target))
                    .setLinearHeadingInterpolation(here.getHeading(), target.getHeading())
                    .addParametricCallback(callbackT, action)
                    .build();
        }
        return follower.pathBuilder()
                .addPath(new BezierLine(here, target))
                .setLinearHeadingInterpolation(here.getHeading(), target.getHeading())
                .build();
    }

    /** הגענו = Pedro סיים, או שאנחנו כבר בתוך סף המרחק. */
    private boolean arrived(Pose target) {
        return !follower.isBusy()
                || follower.getPose().distanceFrom(target) < ARRIVE_THRESHOLD_IN;
    }

    /** Timeout = המצב נמשך יותר מדי, או ש־Pedro מזהה שהרובוט תקוע. */
    private boolean timedOut() {
        return stateTimer.getElapsedTimeSeconds() > PATH_TIMEOUT_SEC || follower.isRobotStuck();
    }

    private void stopMechanisms() {
        intake.setPower(0);
        transfer.setPower(0);
    }

    private void setState(State newState) {
        state = newState;
        stateTimer.resetTimer();
    }

    @Override
    public void stop() {
        follower.breakFollowing();
        stopMechanisms();
    }
}
```

**מה קורה כאן:**

- **זרימת המצבים:**
  ```
  START_CYCLE → DRIVING_TO_COLLECT ─(הגענו)→ COLLECTING → GO_SCORE → DRIVING_TO_SCORE → SCORING ─┐
        ▲                         └─(Timeout)──────────────┘                                    │
        └──────────────────── (נשארו נקודות וזמן) ──────────────────────────────────────────────┘
                                                                     (אחרת) → GO_PARK → PARKING → DONE
  ```
- **`addParametricCallback(0.5, action)`** — "כשהרובוט עבר 50% מהקטע, תריץ את `action`". ה־`t` הוא מספר בין 0 (תחילת הקטע) ל־1 (סופו). ה־Callback חל על הקטע האחרון שנוסף ל־Builder לפניו.
- **מצבי "GO_..." ו־"DRIVING_..."** — מצב GO בונה מסלול ומתחיל לנסוע (רץ פעם אחת), מצב DRIVING מחכה. ההפרדה הזו מונעת את הבאג הקלאסי של קריאה ל־`followPath` בכל סיבוב (פרק 6).
- **`follower.breakFollowing()`** אחרי Timeout — חובה. אחרת Pedro ממשיך לנסות להגיע ליעד הישן, בזמן שאנחנו כבר רוצים לנסוע למקום אחר.
- **`pathFromHereTo()` מחזיר `null`** אם הרובוט כבר נמצא ביעד. מסלול באורך 0 עלול לגרום לחישובים לא תקינים ב־Pedro, אז פשוט לא בונים אותו.
- **המונה `timeouts`** מוצג ב־telemetry. אם אחרי ריצה הוא לא 0 — יודעים בדיוק שמשהו לא הגיע בזמן, ואפשר לחקור.

**🏋️ תרגיל:** כרגע, אם נסיעה לאיסוף נכשלת (Timeout), הרובוט נוסע לנקד בלי כלום. שנו את הלוגיקה כך שבמקרה כזה הוא ידלג ישר למחזור הבא (או לחנייה), בלי לבזבז זמן על ניקוד ריק.

---

## 5. איך להריץ ולבדוק (Workflow)

### שלב 1 — כיול Pedro (פעם אחת לכל רובוט, ושוב אחרי כל שינוי מכני)

בלי כיול, Pedro לא יודע כמה הרובוט שוקל, כמה מהר הוא נוסע ואיפה גלגלי האודומטריה — והמסלולים ייצאו עקומים. מריצים את `Tuning` (TeleOp בקבוצה `Pedro Pathing`) ובוחרים כלי מהתפריט, **לפי הסדר:**

1. **Localization** — `Localization Test`: דוחפים את הרובוט ביד ובודקים שה־X, ה־Y וה־Heading משתנים נכון (קדימה = X עולה, שמאלה = Y עולה, סיבוב נגד השעון = זווית עולה). אחר כך `Offsets Tuner`, `Forward Tuner`, `Lateral Tuner`, `Turn Tuner`.
2. **Automatic** — `Forward Velocity Tuner`, `Lateral Velocity Tuner`, `Forward/Lateral Zero Power Acceleration Tuner`.
3. **Manual** — `Translational`, `Heading`, `Drive`, `Centripetal Tuner` (מקדמי PIDF).
4. **Tests** — `Line`, `Triangle`, `Circle`: אם הרובוט עושה אותם יפה, הכיול טוב.

כל ערך שמודדים מעדכנים ב־**`Constants.java`** — ורק שם. לפרטים על כל כלי: [התיעוד של Pedro](https://pedropathing.com/).

### שלב 2 — כתיבת הקוד

1. יוצרים קובץ חדש **בתוך TeamCode** — למשל בתיקייה `training/guide/` או בתיקייה משלכם תחת `OpMode/Auto/`.
2. מעתיקים את הדוגמה הקרובה ביותר למה שאתם רוצים, ומשנים את שם המחלקה ואת ה־`name` ב־`@Autonomous` (שני OpModes עם אותו `name` = שגיאה).
3. מתכננים את הנקודות ב־[Visualizer](https://visualizer.pedropathing.com/) ומעתיקים אותן לקוד, עם `Math.toRadians` לכל זווית.

### שלב 3 — בנייה

**Build → Make Project** ב־Android Studio, או בטרמינל:

```bash
gradlew.bat :TeamCode:assembleDebug        # Windows
sh ./gradlew :TeamCode:assembleDebug       # macOS / Linux
```

שגיאה אדומה? קוראים את השורה הראשונה שלה — היא כמעט תמיד אומרת באיזה קובץ ובאיזו שורה הבעיה.

### שלב 4 — העלאה לרובוט

1. מתחברים לרשת ה־Wi-Fi של ה־Control Hub (או בכבל USB).
2. בוחרים את המכשיר ב־Android Studio ולוחצים **Run** (המשולש הירוק) עם `TeamCode`.
3. ב־Driver Station בודקים שה־Robot Configuration כולל את כל השמות: `FL`, `FR`, `BL`, `BR`, `pinpoint`, וגם `lift`/`claw`/`intake`/`transfer` לפי הדוגמה.

### שלב 5 — ריצה ראשונה זהירה

1. **מוחקים את `@Disabled`** בקובץ, בונים ומעלים שוב.
2. **מקטינים את המהירות** לניסיון הראשון: מוסיפים ב־`init()` אחרי יצירת ה־Follower את השורה `follower.setMaxPower(0.4);`.
3. **מפנים את השטח** סביב המסלול. אחד מחברי הקבוצה עומד ליד ה־Driver Station עם האצבע על **STOP**.
4. **מציבים את הרובוט בדיוק** במיקום ובזווית של `startPose`. סטייה של 2 אינץ' בהתחלה = סטייה של 2 אינץ' לאורך כל המסלול.
5. INIT → בודקים ב־telemetry שה־X, ה־Y וה־Heading תואמים ל־`startPose` → START.

### שלב 6 — בדיקה ושיפור

- **telemetry** — מסתכלים על ה־State וה־Pose. אם הרובוט עצר, ה־State אומר איפה בדיוק.
- **לוח בקרה** — הפרויקט כולל את FTC Dashboard ואת Panels. אפשר לראות בהם את המיקום והמסלול על מפת מגרש בזמן אמת.
- משנים **דבר אחד בכל פעם**, מריצים שוב ורושמים מה השתנה.
- כשהכל עובד במהירות נמוכה — מעלים בהדרגה את `setMaxPower` (או מוחקים את השורה, ואז העוצמה המקסימלית היא 1.0).

---

## 6. תקלות נפוצות ואיך לפתור אותן

הדרך הנכונה לדבג: **לא מנחשים.** קודם מסתכלים על ה־telemetry (State, X, Y, Heading), מבינים מה הרובוט *חושב* שקורה, ורק אז מתקנים.

### טבלה מהירה

| מה רואים | הסיבה הסבירה | מה בודקים / מתקנים |
|---|---|---|
| הרובוט לא זז בכלל | שכחתם `follower.update()`, או שה־OpMode לא באמת התחיל נסיעה | `follower.update()` בשורה הראשונה של `loop()`; לבדוק שמצב 0 קורא ל־`followPath` |
| ה־OpMode לא מופיע ב־Driver Station | יש `@Disabled`, או שה־`name` כפול | למחוק `@Disabled`; לוודא שם ייחודי; לבנות ולהעלות שוב |
| קריסה ב־INIT עם `... not found` | שם חומרה בקוד לא תואם ל־Configuration | להשוות אות־אות, כולל אותיות גדולות (`FL` ≠ `fl`) |
| הרובוט מסתובב בטירוף | זווית במעלות במקום ברדיאנים | `Math.toRadians(90)`, לא `90` |
| הרובוט מסתובב פתאום בתחילת קטע | לא הוגדרה שיטת Heading, ונבחר Tangent כברירת מחדל | להוסיף `setLinearHeadingInterpolation` או `setConstantHeadingInterpolation` |
| המסלול נכון בצורה אבל מוזז | `startPose` בקוד לא תואם למקום שבו הרובוט עומד | להציב את הרובוט בדיוק; להשוות ל־telemetry ב־INIT |
| הרובוט נוסע לכיוון הפוך / במראה | בעיית כיוון באודומטריה או במנועים | `Localization Test`: קדימה = X עולה, שמאלה = Y עולה. אם לא — כיוונים ב־`Constants` |
| נוסע חצי מהמרחק או כפול ממנו | יחידות: סנטימטרים במקום אינצ'ים, או כיול שגוי של Forward/Lateral | הכל באינצ'ים; להריץ שוב `Forward Tuner` / `Lateral Tuner` |
| עוקף את היעד ומתנדנד סביבו | מקדמי PIDF גבוהים מדי, או מהירות גבוהה מדי | `setMaxPower(0.5)` לבדיקה; לכייל מחדש בכלים הידניים |
| עוצר לפני היעד ולא מתקן | מקדמי PIDF נמוכים מדי, או `holdEnd = false` | כיול ידני; `followPath(path, true)` |
| עקומות "נמרחות" החוצה | `centripetalScaling` לא מכויל, או מהירות גבוהה בפנייה | `Centripetal Tuner`; להוריד מהירות בקטע הזה |
| נתקע במצב מסוים לנצח | תנאי מעבר שלא מתקיים אף פעם | ראו פירוט למטה |
| המיקום ב־telemetry הוא `NaN` | ה־Pinpoint לא מחובר או לא מוגדר | שם `pinpoint` ב־Configuration; כבל; `follower.isLocalizationNAN()` |
| הרובוט "נלחם בעצמו" ורועד | שני Followers באותו OpMode | לא לבנות `MMRobot` במצב AUTO באותו OpMode עם `Constants.createFollower` |

### "הרובוט סוטה מהמסלול"

1. **קודם כל — Localization.** הריצו `Localization Test`, דחפו את הרובוט ביד בדיוק 48 אינץ' (שני אריחים) ובדקו שה־telemetry מראה 48 בערך. אם המיקום שגוי, אין טעם לגעת ב־PIDF: Pedro מתקן לפי מידע לא נכון.
2. **מהירות.** הריצו את אותו מסלול עם `setMaxPower(0.3)`. אם במהירות נמוכה המסלול מדויק — הבעיה בכיול הדינמי (Zero Power Acceleration, PIDF), לא בקוד.
3. **מצב הגלגלים והמגרש.** גלגל אודומטריה שלא נוגע ברצפה, או ברגים רופפים, ייתנו תוצאות משתנות מריצה לריצה.

### "בעיות יחידות מידה"

- **מרחקים — אינצ'ים בלבד.** 1 אינץ' = 2.54 ס"מ. אריח מגרש = 24 אינץ'. מדדתם 60 ס"מ? זה `60 / 2.54 ≈ 23.6`.
- **זוויות — רדיאנים בלבד** בכל מה שנכנס ל־Pedro. ב־telemetry מציגים במעלות (`Math.toDegrees`) רק כדי שיהיה קל לקרוא.
- **זמן — לשים לב למי מדבר.** `Timer.getElapsedTimeSeconds()` מחזיר שניות. `setTimeoutConstraint(...)` ב־PathBuilder מקבל **מילישניות**. `WaitCommand(...)` של SolversLib מקבל **מילישניות**.
- **מעלית — טיקים, לא סנטימטרים.** כמה טיקים זה סנטימטר תלוי במנוע ובגלגלת. מודדים פעם אחת ורושמים.

### "האוטונומי נתקע במצב מסוים"

כש־telemetry מראה את אותו State שוב ושוב, שואלים: **איזה תנאי צריך להתקיים כדי לצאת מהמצב הזה, ולמה הוא לא מתקיים?**

- **`isBusy()` לא הופך ל־`false`** — הרובוט לא מצליח להגיע לנקודה (קיר, מכשול, נקודה מחוץ למגרש), או שהוא מתנדנד סביבה בלי להתייצב. **פתרון:** להוסיף Timeout למצב, כמו ב־`timedOut()` של דוגמה 5.
- **`followPath` נקרא בכל סיבוב** — אם קוראים ל־`followPath` במצב שגם *מחכה* לסיום, המסלול מתחיל מחדש בכל סיבוב והרובוט לעולם לא מגיע. **פתרון:** מצב אחד מתחיל את הנסיעה ועובר מיד למצב הבא; מצב אחר מחכה.

  ```java
  // ❌ באג: מתחיל את המסלול מחדש עשרות פעמים בשנייה
  case 0:
      follower.followPath(path, true);
      if (!follower.isBusy()) pathState = 1;
      break;

  // ✅ נכון: מצב שמתחיל, ומצב שמחכה
  case 0:
      follower.followPath(path, true);
      pathState = 1;
      break;
  case 1:
      if (!follower.isBusy()) pathState = 2;
      break;
  ```

- **הטיימר לא אופס** — בודקים `pathTimer.getElapsedTimeSeconds() > 0.5` אבל הטיימר רץ מתחילת האוטונומי, אז התנאי מתקיים מיד (או ההפך — הטיימר אופס בכל סיבוב, ואף פעם לא מגיע ל־0.5). **פתרון:** לאפס רק במעבר מצב, דרך `setPathState` / `setState`.
- **מנגנון שלא מגיע ליעד** — המעלית עוצרת 20 טיקים מתחת ליעד בגלל משקל, והתנאי `liftAtTarget()` אף פעם לא מתקיים. **פתרון:** סובלנות סבירה (`LIFT_TOLERANCE`) + Timeout.
- **שכחתם `break`** ב־`switch` — הקוד "נופל" למצב הבא באותו סיבוב ומדלג על שלבים. בכל `case` — `break` בסוף.
- **`while` או `sleep` בתוך `loop()`** — כל עוד הלולאה הפנימית רצה, `follower.update()` לא נקרא, Pedro "מתעוור" והרובוט ממשיך עם הכוח האחרון. לעולם לא.

### טיפים כלליים לדיבאגינג

- **telemetry עשיר:** State, זמן במצב, Pose, מרחק ליעד, מספר Timeouts. כל מה שבדוגמה 5 — ואפילו יותר.
- **בודקים כל קטע לבד:** מסלול ארוך לא עובד? שימו `pathState = -1` אחרי הקטע הראשון, ותוודאו שהוא תקין לפני שממשיכים.
- **מצלמים את הריצה בטלפון.** בהילוך איטי רואים דברים שהעין מפספסת.
- **מתעדים:** "שיניתי X מ־0.17 ל־0.2 → הרובוט התנדנד פחות". בעוד שבועיים לא תזכרו.
- **Git הוא החבר שלכם:** `commit` לפני כל שינוי גדול, כדי שתמיד יהיה אפשר לחזור לגרסה שעבדה (שיעור 6).

---

## 7. צ'קליסט לפני כל ריצה

- [ ] כל השינויים נעשו **רק בתוך `TeamCode`**
- [ ] Pedro מכויל, ו־`Line` / `Triangle` ב־`Tuning` עובדים יפה
- [ ] כל הזוויות בקוד עטופות ב־`Math.toRadians(...)`
- [ ] לכל `addPath(...)` יש שיטת Heading מוגדרת במפורש
- [ ] `follower.update()` היא השורה הראשונה ב־`loop()`
- [ ] לכל מצב שמחכה יש תנאי יציאה, ולכל המתנה ארוכה יש Timeout
- [ ] אין `while` ואין `sleep` בתוך `loop()`
- [ ] שמות החומרה בקוד זהים ל־Robot Configuration
- [ ] `@Disabled` נמחק, וה־`name` ייחודי
- [ ] הרובוט מוצב בדיוק ב־`startPose`, וה־telemetry ב־INIT מאשר את זה
- [ ] השטח פנוי, ומישהו מוכן עם האצבע על STOP

---

**בהצלחה! 🐝** אם משהו לא ברור — שואלים מנטור, ומתעדים את התשובה בשביל מי שיבוא אחריכם.
