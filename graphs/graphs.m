%close all
clear all
M = csvread('40k/jobTimes0.csv');
times = M(:,3);
times = times.';
figure
plot(times);
xlabel('Job number');
ylabel('Job Duration in ms');
hold on
avg = sum(times)/length(times);
means = ones(1,length(times));
means = avg.*means;
plot(means,'r')
v = var(times);
legend('Job execution time', 'Average job execution time');
