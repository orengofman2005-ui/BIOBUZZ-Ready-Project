package org.firstinspires.ftc.teamcode.training.auto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

/** גאומטריית שיעור 10 ל־Pedro 2.1.2. אינה יוצרת Follower נוסף או מפעילה מנועים. */
public final class DemoPathFactory {
    private DemoPathFactory() {}
    public static PathChain shortLine(Follower follower) {
        Pose start = new Pose(0, 0, 0);
        Pose end = new Pose(12, 0, 0);
        return follower.pathBuilder()
                .addPath(new BezierLine(start, end))
                .setLinearHeadingInterpolation(start.getHeading(), end.getHeading())
                .build();
    }
}
