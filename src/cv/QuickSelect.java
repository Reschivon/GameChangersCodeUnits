package cv;

class QuickSelect {

    public static void main(String[] args) {
        short[] arr = new short[]{3,9,4,5,6,2,7,8, 1};
        System.out.println(findKthLargest(arr, arr.length/2+1));
        //prints 5
    }

    public static int median(short[] nums){
        return findKthLargest(nums, nums.length/2+1);
    }

    public static int findKthLargest(short[] nums, int k) {
        int start = 0, end = nums.length - 1, index = nums.length - k;
        while (start < end) {
            int pivot = partion(nums, start, end);
            if (pivot < index) start = pivot + 1;
            else if (pivot > index) end = pivot - 1;
            else return nums[pivot];
        }
        return nums[start];
    }

    private static int partion(short[] nums, int start, int end) {
        int pivot = start;
        short temp;
        while (start <= end) {
            while (start <= end && nums[start] <= nums[pivot]) start++;
            while (start <= end && nums[end] > nums[pivot]) end--;
            if (start > end) break;
            temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
        }
        temp = nums[end];
        nums[end] = nums[pivot];
        nums[pivot] = temp;
        return end;
    }
}
