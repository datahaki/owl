![alpine_877](https://user-images.githubusercontent.com/4012178/116814864-1b1a1580-ab5b-11eb-97e6-1441af4ececa.png)

# ch.alpine.owl

Library for motion planning in Java

![](https://github.com/datahaki/owl/actions/workflows/mvn_test.yml/badge.svg)

The library was developed with the following objectives in mind
* suitable for use in safety-critical real-time systems
* trajectory planning for an autonomous vehicle
* implementation of theoretical concepts with high level of abstraction
* simulation and visualization

<table>
<tr>
<td>

![usecase_motionplan](https://user-images.githubusercontent.com/4012178/35968244-96577dee-0cc3-11e8-80a1-b38691e863af.png)

Motion planning

<td>

![shadow_regions](https://user-images.githubusercontent.com/4012178/42315433-b53034de-8047-11e8-8fc2-87fa504460c5.png)

Obstacle anticipation

<td>

![usecase_gokart](https://user-images.githubusercontent.com/4012178/35968269-a92a3b46-0cc3-11e8-8d5e-1276762cdc36.png)

[Trajectory pursuit](https://www.youtube.com/watch?v=XgmS8CP6gqw)

<td>

![planning_obstacles](https://user-images.githubusercontent.com/4012178/40268689-2af06cd4-5b72-11e8-95cf-d94edfdc3dd1.png)

[Static obstacles](https://www.youtube.com/watch?v=xLZeKFeAokM)

</tr>
</table>

## 🎓 Student Projects

### 2017

* Jonas Londschien (MT): *An Anytime Generalized Label Correcting Method for Motion Planning*

### 2018

* Yannik Nager (MT): *What lies in the shadows? Safe and computation-aware motion planning for autonomous vehicles using intent-aware dynamic shadow regions*

### 2019

* André Stoll (MT): *Multi-Objective Optimization Using Preference Structures*
* Oliver Brinkmann (MT): *Averaging on Lie Groups: Applications of Geodesic Averages and Biinvariant Means*
* Joel Gächter (MT): *Subdivision-Based Clothoids in Autonomous Driving*

## 🏆 Features

* Motion planning algorithms: [GLC](src/main/java/ch/ethz/idsc/owl/glc/std/StandardTrajectoryPlanner.java), and [RRT*](src/main/java/ch/ethz/idsc/owl/rrts/core/DefaultRrts.java)
* integrators: Euler, Midpoint, Runge-Kutta 4-5th order, exact integrator for the group SE2
* state-space models: car-like, two-wheel-drive, pendulum-swing-up, Lotka-Volterra, etc.
* efficient heuristic for goal regions: sphere, conic section
* visualizations and animations, see [video](https://www.youtube.com/watch?v=lPQW3GqQqSY)

## 🤖 Motion Planning

### GLC

Rice2: 4-dimensional state space + time

<table>
<tr>
<td>

![rice2dentity_1510227502495](https://user-images.githubusercontent.com/4012178/32603926-dd317aea-c54b-11e7-97ab-82df23b52fa5.gif)

<td>

![rice2dentity_1510234462100](https://user-images.githubusercontent.com/4012178/32608146-b6106d1c-c55b-11e7-918d-e0a1d1c8e400.gif)

</tr>
</table>

---

SE2: 3-dimensional state space

<table>
<tr>
<td>

Car

![se2entity_1510232282788](https://user-images.githubusercontent.com/4012178/32606961-813b05a6-c557-11e7-804c-83b1c5e94a6f.gif)

<td>

Two-wheel drive (with Lidar simulator)

![twdentity_1510751358909](https://user-images.githubusercontent.com/4012178/32838106-2d88fa2c-ca10-11e7-9c2a-68b34b1717cc.gif)

</tr>
</table>

---

Simulation: autonomous gokart or car

<table>
<tr>
<td>

Gokart

![_1530775215911](https://user-images.githubusercontent.com/4012178/42308510-10283bf0-8036-11e8-8a42-b8f1f807bb88.gif)

<td>

Car

![_1530775403211](https://user-images.githubusercontent.com/4012178/42308523-1ae4ea8e-8036-11e8-8067-83bdd67a2d33.gif)

</tr>
</table>


### RRT*

R^2

![r2ani](https://cloud.githubusercontent.com/assets/4012178/26282173/06dccee8-3e0c-11e7-930f-fedab34fe396.gif)

![r2](https://cloud.githubusercontent.com/assets/4012178/26045794/16bd0a54-394c-11e7-9d11-19558bc3be88.png)

### Nearest Neighbors

<table>
<tr>
<td>

![nearest_r2](https://user-images.githubusercontent.com/4012178/64911097-dc351300-d71d-11e9-9a92-5ce1fcd8c42f.png)

R^2

<td>

![nearest_dubins](https://user-images.githubusercontent.com/4012178/64911102-e7883e80-d71d-11e9-96d2-11273b892775.png)

Dubins

<td>

![nearest_clothoid](https://user-images.githubusercontent.com/4012178/64911109-f242d380-d71d-11e9-83cf-358a4047175b.png)

Clothoid

</tr>
</table>

## 👥 Contributors

Jan Hakenberg, Jonas Londschien, Yannik Nager, André Stoll, Joel Gaechter

> The code in the repository operates a heavy and fast robot that may endanger living creatures. We follow best practices and coding standards to protect from avoidable errors.

## 📰 Publications

* *What lies in the shadows? Safe and computation-aware motion planning for autonomous vehicles using intent-aware dynamic shadow regions*
by Yannik Nager, Andrea Censi, and Emilio Frazzoli,
[video](https://www.youtube.com/watch?v=3w6zQF9HOAM)

## 📚 References

* *A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning*
by Brian Paden and Emilio Frazzoli, 
[arXiv:1607.06966](https://arxiv.org/abs/1607.06966),
[video](https://www.youtube.com/watch?v=4-r6Oi8GHxc)
* *Sampling-based algorithms for optimal motion planning*
by Sertac Karaman and Emilio Frazzoli,
[IJRR11](http://ares.lids.mit.edu/papers/Karaman.Frazzoli.IJRR11.pdf)

---

![ethz300](https://user-images.githubusercontent.com/4012178/45925071-bf9d3b00-bf0e-11e8-9d92-e30650fd6bf6.png)
