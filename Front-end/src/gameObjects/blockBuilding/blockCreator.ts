/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Block from './blocks/block';

/**
 * BlockCreator is a delegate function that defines the creation of a Block, given an x and y coordinate.
 * This is useful as various Block subclasses have different constructor signatures.
 * BlockCreator provides a common way to instantiate any Block, regardless of what subclass it is, using only one (x, y) coordinate.
 */
export type BlockCreator = (x: number, y: number) => Block;

export default BlockCreator;