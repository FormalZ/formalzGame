// Code based on: https://www.dustinhorne.com/post/2016/06/09/implementing-a-dictionary-in-typescript
/**
 * Generic Dictionary class.
 * The key must always be a string, the value can be any type.
 */
export default class Dictionary<T> {
    private items: { [key: string]: T };

    private count: number = 0;

    public constructor() {
        this.items = {};
    }

    /**
     * Adds a new key value pair to the Dictionary.
     * If there already exists an entry with the same key in the Dictionary, it will be overridden.
     * @param key the key of the key value pair.
     * @param value the value of the key value pair.
     */
    public add(key: string, value: T): void {
        if (!this.items.hasOwnProperty(key)) {
            this.count++;
        }

        this.items[key] = value;
    }

    /**
     * Removes a key value pair from the Dictionary based on a key.
     * @param key the key of the key value pair to be removed.
     */
    public remove(key: string): void {
        if (this.items.hasOwnProperty(key)) {
            delete this.items[key];
            this.count--;
        }
    }

    /**
     * @returns a list of all keys in the Dictionary.
     */
    public getKeys(): string[] {
        const keys: string[] = [];

        for (let prop in this.items) {
            keys.push(prop);
        }

        return keys;
    }

    /**
     * @returns a list of all values in the Dictionary.
     */
    public getValues(): T[] {
        const values: T[] = [];

        for (let prop in this.items) {
            values.push(this.items[prop]);
        }

        return values;
    }

    /**
     * Clears all entries in the Dictionary.
     */
    public clear(): void {
        this.getKeys().forEach(key => this.remove(key), this);
    }

    /**
     * Retrieves a value from the Dictionary based on a given key.
     * @param key the key of the key value pair.
     * @returns the value corresponding to the specified key.
     */
    public get(key: string): T { return this.items[key]; }

    /**
     * Checks whether an entry exists in this Dictionary that has the specified key.
     * @param key the key to check.
     * @returns whether a key value pair exists in this Dictionary with the given key.
     */
    public containsKey(key: string): boolean { return this.items.hasOwnProperty(key); }
}