import { expect } from 'chai';
import * as Game from '../../src/app';
import * as QuestionGameTask from '../../src/gameTasks/questionGameTask';
import * as Level from '../../src/states/level';

describe('QuestionGameTask', () => {
    /*
    let game = Game.startApp();
    let level = new Level.default();
    let qgt = new QuestionGameTask.default(level);

    describe('On construction', function() {
        it('should be defined', function() {
            expect(qgt).to.exist;
        });
    });

    describe('preFeedback', function() {
        it('with no feedback', function() {
            let commandRun: boolean = qgt.tryCommand('preFeedback', '');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPreFeedback()).to.equal('');
        });
        it('with single element in array', function() {
            let commandRun: boolean = qgt.tryCommand('preFeedback', '[[a, 1]]');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPreFeedback()).to.equal(`have you tried thinking of a with 1?`);
        });
        it('with multiple elements in array', function() {
            let commandRun: boolean = qgt.tryCommand('preFeedback', '[[a, 1];[b, true]]');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPreFeedback()).to.equal(`have you tried thinking of a with 1?\nhave you tried thinking of b with true?`);
        });
    });

    describe('postFeedback', function() {
        it('with no feedback', function() {
            let commandRun: boolean = qgt.tryCommand('postFeedback', '');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPostFeedback()).to.equal('');
        });
        it('with single element in array', function() {
            let commandRun: boolean = qgt.tryCommand('postFeedback', '[[a, 1]]');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPostFeedback()).to.equal(`have you tried thinking of a with 1?`);
        });
        it('with multiple elements in array', function() {
            let commandRun: boolean = qgt.tryCommand('postFeedback', '[[a, 1];[b, true]]');
            expect(commandRun).to.be.a('boolean').and.to.equal(true);
            expect(level.getPostFeedback()).to.equal(`have you tried thinking of a with 1?\nhave you tried thinking of b with true?`);
        });
    });
    */
});