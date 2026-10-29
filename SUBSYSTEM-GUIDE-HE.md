# 🐝 איך בונים Subsystem ומחברים אותו לרובוט

מדריך קצר: בונים מנגנון חדש מאפס, ומגיעים למצב שבו הוא באמת זז כשלוחצים על כפתור.
הדוגמה היא **רולר** — מנוע אחד שאוסף פנימה או פולט החוצה. כל מנגנון אחר נבנה באותה דרך בדיוק.

> ⛔ **כל השינויים נעשים רק בתוך `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.**

---

## מה זה Subsystem?

**Subsystem = מנגנון אחד ברובוט.** מעלית, תופסן, רולר, יורה — לכל אחד מחלקה משלו.

ה־Subsystem עושה שני דברים:
1. **מחזיק את החומרה** של המנגנון (מנועים, סרבו, חיישנים).
2. **נותן פקודות (Commands)** — פעולות מוכנות כמו "אסוף", "פלוט", "עצור".

כך, כשרוצים להפעיל את המנגנון, לא כותבים `motor.setPower(...)` בכל מקום. כותבים `rollerSubsystem.inCommand()`, וכל ההגדרות נמצאות במקום אחד.

**4 שלבים לחיבור:**

```
1. יוצרים את הקובץ       →  SubSystems/RollerSubsystem.java
2. רושמים אותו ברובוט      →  MMSystems.java
3. דואגים שייעצר ב־STOP    →  MMOpMode.java
4. מחברים לכפתורים         →  OpMode/TeleOp/RollerTeleOp.java
```

---

## שלב 1 — יוצרים את ה־Subsystem

ב־Android Studio: קליק ימני על התיקייה `SubSystems` ← **New → Java Class** ← `RollerSubsystem`.

```java
package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

public class RollerSubsystem extends SubsystemBase {

    // 1. החומרה של המנגנון
    private final DcMotor motor;

    // 2. מספרים שכדאי לשנות במקום אחד
    private static final double IN_POWER = 0.8;
    private static final double OUT_POWER = -0.8;

    // 3. הבנאי: מחברים את המנוע מה־Configuration ומגדירים אותו
    public RollerSubsystem() {
        motor = MMRobot.getInstance().hardwareMap.get(DcMotor.class, "rollerMotor");
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(0);
    }

    // 4. פעולות בסיס
    public void setPower(double power) {
        motor.setPower(power);
    }

    public void stop() {
        motor.setPower(0);
    }

    // 5. פקודות: מה שהשלט וה־OpMode מפעילים
    public Command inCommand() {
        return new InstantCommand(() -> setPower(IN_POWER), this);
    }

    public Command outCommand() {
        return new InstantCommand(() -> setPower(OUT_POWER), this);
    }

    public Command stopCommand() {
        return new InstantCommand(this::stop, this);
    }
}
```

**מה כל חלק עושה:**

| חלק | הסבר |
|---|---|
| `extends SubsystemBase` | הופך את המחלקה ל־Subsystem שה־Scheduler מכיר. |
| `"rollerMotor"` | **השם חייב להיות זהה בדיוק** לשם ב־Robot Configuration, כולל אותיות גדולות. |
| `setDirection` | אם המנגנון מסתובב הפוך — משנים ל־`REVERSE`, לא הופכים את כל המספרים. |
| `BRAKE` | כשהעוצמה 0, המנוע "בולם" ולא ממשיך להסתובב מהתנופה. |
| `InstantCommand(..., this)` | פקודה שרצה פעם אחת ומסתיימת מיד. ה־`this` אומר "הפקודה הזו שייכת לרולר", כדי ששתי פקודות לא יפעילו את הרולר באותו זמן. |

---

## שלב 2 — רושמים את ה־Subsystem ברובוט (`Libraries/MMLib/MMSystems.java`)

כאן הרובוט "מכיר" את המנגנונים שלו. מוסיפים **4 שורות**:

**א. Import** — למעלה, ליד שאר ה־imports של `SubSystems`:
```java
import org.firstinspires.ftc.teamcode.SubSystems.RollerSubsystem;
```

**ב. שדה** — ליד שאר ה־Subsystems (`public DriveTrain driveTrain;` וכו'):
```java
public RollerSubsystem rollerSubsystem;
```

**ג. יצירה** — בתוך `initSubsystems()`, מיד אחרי `this.driveTrain = new DriveTrain();`:
```java
this.rollerSubsystem = new RollerSubsystem();
```

**ד. ניקוי** — בתוך `clearSystems()`, ליד שאר שורות ה־`null`:
```java
rollerSubsystem = null;
```

מעכשיו, מכל מקום בקוד: `MMRobot.getInstance().rollerSubsystem`.

---

## שלב 3 — עצירה בטוחה ב־STOP (`Libraries/MMLib/MMOpMode.java`)

ב־`end()` יש רשימה של כל המנגנונים שנעצרים כשה־OpMode נגמר. מוסיפים את הרולר, ליד שורת ה־`shooterSubsystem`:

```java
if (robot.rollerSubsystem != null) cleanup.add(robot.rollerSubsystem::stop);
```

> ⚠️ **לא לדלג!** בלי השורה הזו, אם הרולר פעל ברגע שלחצו STOP — הוא עלול להמשיך להסתובב.

---

## שלב 4 — מחברים לכפתורים (`OpMode/TeleOp/RollerTeleOp.java`)

קובץ חדש בתיקייה `OpMode/TeleOp`:

```java
package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

@TeleOp(name = "Roller Test", group = "Learning")
public class RollerTeleOp extends MMOpMode {

    @Override
    public void initialize() {
        MMRobot robot = MMRobot.getInstance();
        robot.initJeruRobot().build(this);   // בונה את הרובוט המלא, כולל rollerSubsystem

        // A מוחזק = אוסף, B מוחזק = פולט, שחרור = עצירה
        robot.gamepadEx1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.rollerSubsystem.inCommand())
                .whenReleased(robot.rollerSubsystem.stopCommand());

        robot.gamepadEx1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.rollerSubsystem.outCommand())
                .whenReleased(robot.rollerSubsystem.stopCommand());
    }
}
```

**מה קורה כאן:**
- `initJeruRobot().build(this)` — בונה את הרובוט המלא: הנעה (עם נהיגה בסטיקים) וגם הרולר.
- `whenPressed(...)` — ברגע הלחיצה, מתזמנים את הפקודה. `whenReleased(...)` — ברגע השחרור.
- אין צורך לכתוב `run()`: `MMOpMode` כבר מריץ את כל הפקודות בכל סיבוב.

---

## שלב 5 — מריצים

1. **Robot Configuration** ב־Driver Station: מוסיפים מנוע בשם `rollerMotor` בפורט שאליו הוא מחובר. (ארבעת מנועי ההנעה `FL`, `FR`, `BL`, `BR` צריכים להיות שם כבר.)
2. **Build → Make Project**, ואז **Run** על ה־Control Hub.
3. ב־Driver Station בוחרים **Roller Test** ← INIT ← START.
4. מחזיקים A — הרולר אוסף. מחזיקים B — פולט. משחררים — עוצר.

---

## המנגנון לא זז? בודקים לפי הסדר

| מה רואים | מה בודקים |
|---|---|
| שגיאה ב־INIT: `Unable to find a hardware device with name "rollerMotor"` | השם ב־Configuration שונה מהשם בקוד (אותיות גדולות!). |
| שגיאה ב־INIT על `FL` / `FR` / `BL` / `BR` | הרובוט המלא בונה גם את ההנעה. כל ארבעת המנועים חייבים להיות ב־Configuration. |
| `NullPointerException` על `rollerSubsystem` | שכחתם את שורה **ג'** בשלב 2 (`new RollerSubsystem()`). |
| הכל עולה, אבל לחיצה לא עושה כלום | לחצתם על השלט הנכון? (`gamepadEx1` = שלט 1.) המנוע מחובר לפורט שהוגדר? |
| מסתובב הפוך | `Direction.FORWARD` ← `REVERSE` בבנאי. |
| ממשיך להסתובב אחרי STOP | שכחתם את שלב 3. |

---

## רוצים מנגנון עם סרבו?

אותם 4 שלבים. רק החומרה והפעולות משתנות:

```java
private final Servo servo;   // import com.qualcomm.robotcore.hardware.Servo;

public ClawSubsystem() {
    servo = MMRobot.getInstance().hardwareMap.get(Servo.class, "clawServo");
}

public Command openCommand()  { return new InstantCommand(() -> servo.setPosition(0.55), this); }
public Command closeCommand() { return new InstantCommand(() -> servo.setPosition(0.45), this); }
```

> 💡 לסרבו אין `stop()` — הוא נשאר במיקום האחרון, ולכן אין צורך בשלב 3. התחילו מטווח צר (0.45–0.55) ותרחיבו רק אחרי שבדקתם שהמנגנון לא נתקע.
