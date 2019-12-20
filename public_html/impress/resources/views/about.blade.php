{{-- This program has been developed by students from the bachelor Computer Science 
at Utrecht University within the Software and Game project course (2018)
©Copyright Utrecht University (Department of Information and Computing Sciences) --}}
@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="panel panel-default">
                <div class="panel-heading">About the Project</div>

                <div class="panel-body">
                    Welcome to FormalZ!
                    <br /> <br />
                    FormalZ is a serious game created for the IMPRESS project, which is an international project to improve the engagement of software engineering students through gamification.
                    The game's main purpose is to introduce students to formal specifications. By making use of this website, teachers can create certain specifications and assign these to their students, which allows them to learn and prepare for more difficult problems in the future.
                    FormalZ consists of multiple components, which all work hand in hand to make sure everything runs smoothly.
                    These are: a website with an integrated TypeScript game, a Java back end to handle most communication and event handling, and a database structure to support these components.
                    <br /> <br />
                    The website makes this game available to use in a teacher-student environment.
                    Teachers are granted an account by an admin.
                    With this account they are able to create their own classroom and invite students to this classroom by email or by sharing a link.
                    A student can only join a classroom when given an invite link.
                    They must use a username which is shown to other students and the teacher instead of their real name.
                    <br /> <br />
                    Teachers can create a level in the classroom by submitting their own problem.
                    They will have to provide data including the correct answer, the description of the function itself, and the types of the variables used in the conditions.
                    After this, a level is available for students to play in this classroom. 
                    This level consists of a series of problems from a repository to prepare for the teacher-submitted problem, and finally the teacher-submitted problem itself.
                    <br /> <br />
                    The game itself is a tower defence game with extra features to make the game 'serious'.
                    In tower defence games, the player has to deflect incoming waves of enemies by building a multitude of towers alongside the path the enemies will follow.
                    The towers have certain strengths and weaknesses and grant the player many possibilities to stay alive as long as possible, as each wave will be a little stronger than the last one.
                    The goal of the player is to survive as long as possible or to make it to the end, which usually entails some kind of extremely difficult final wave.
                    This serves as the climax of the game.
                    <br /> <br />
                    Similarly, in this game the player is constantly progressing to a final wave, which is the problem specified by the teacher.
                    Before this final problem is reached, the player has to make their way through a certain amount of easier problems, which are generally similar to the final problem.
                    These problems are formal specifications that need to be defined and constructed by the player. 
                    The pre- and postconditions that need to be written for a formal specification are built using a variation of block programming:
                    small segments of code are presented to the player, and it is their task to make use of the correct segments and connect them in such a way that they form the necessary condition.
                    <br /> <br />
                    After students have finished the problem submitted by the teacher, they exit the game and get shown an overview screen on the website. 
                    This shows personal statistics to provide feedback and a scoreboard of high scores for the entire level. 
                    The teacher can access a page which shows each student’s email address of the students that have completed the level.
                    This way, a teacher can assign this game as a homework assignment and check if students have completed it or not.
                    Do note that the email addresses are already known to the teacher and are not linked to student usernames.
                    <br /> <br />
                    The Java back end handles important game logic.
                    Its main task is to check the validity of answers by communicating with the provided Haskell checker.
                    The Java back end then sends the game new information depending on the response obtained from the checker.
                    This information can consist of feedback (in the form of a counterexample provided by the Haskell back end), hints, wave information, problem information or other major events, such as finishing the game. 
                    When a user has done the block building necessary to construct a condition, this is sent to the Java back end and checked.
                    This means that a cheating user must still write pre- and postconditions to get registered as having completed the level.
                </div>
            </div>
        </div>
    </div>
</div>
@endsection
