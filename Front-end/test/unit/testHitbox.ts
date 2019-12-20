import { expect } from 'chai';

import Hitbox from '../../src/gameObjects/grid/hitbox';

const cellSize: number = 16;

describe('Hitbox', () => {
    let hitbox: Hitbox = new Hitbox(0, 0, 10, 10);

    describe('on construction', () => {

        it('should be defined', () => {
            expect(hitbox).to.exist;
        });
    });
});
