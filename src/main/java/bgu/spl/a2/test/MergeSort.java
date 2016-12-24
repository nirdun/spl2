/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 100000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
//            System.out.println(Arrays.toString(task.getResult().get()));
            System.out.println("Is sorted: " + IsSorted(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();
    }

    private static boolean IsSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1])
                return false;
        }

        return true;
    }

    @Override
    protected void start() {
        if (array.length > 1) {
            List<Task<int[]>> tasks = new ArrayList<>();
            int center = array.length / 2;
            Task<int[]> left = new MergeSort(Arrays.copyOfRange(array, 0, center));
            Task<int[]> right = new MergeSort(Arrays.copyOfRange(array, center, array.length));

            tasks.add(left);
            tasks.add(right);
            spawn(left, right);

            // Merge when children have finished sorting
            whenResolved(tasks, () -> {
                merge(left.getResult().get(), right.getResult().get());

                // Call complete on sorted array
                complete(array);
            });
        } else {
            // Call complete on a single cell array
            complete(array);
        }
    }

    /**
     * Merge two sorted arrays to a single sorted array
     *
     * @param sortedArr1 First sorted array
     * @param sortedArr2 Second sorted array
     */
    private void merge(int[] sortedArr1, int[] sortedArr2) {
        int arr1 = 0;
        int arr2 = 0;
        int orgIndex = 0;

        // Override original array with same values but sorted
        while (arr1 < sortedArr1.length && arr2 < sortedArr2.length) {
            if (sortedArr1[arr1] <= sortedArr2[arr2]) {
                array[orgIndex++] = sortedArr1[arr1++];
            } else {
                array[orgIndex++] = sortedArr2[arr2++];
            }
        }

        // Copy rest of arr1
        while (arr1 < sortedArr1.length) {
            array[orgIndex++] = sortedArr1[arr1++];
        }

        // Copy rest of arr2
        while (arr2 < sortedArr2.length) {
            array[orgIndex++] = sortedArr2[arr2++];
        }
    }
}